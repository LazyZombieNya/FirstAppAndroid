package ru.netology.nmedia.repository
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import retrofit2.Response
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import javax.inject.Inject


class  PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostsApiService,
) : PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)
    private var responseErrMess: Pair<Int, String> = Pair(0, "")
    override fun getErrMess(): Pair<Int, String> {
        return responseErrMess
    }

    override suspend fun readAllPost() {
        dao.readAll()
    }



    private suspend fun saveMedia(file: File):Response<Media>{
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return apiService.saveMedia(part)
    }

    override suspend fun requestToken(login: String, password: String): Token {
        try {
            val response = apiService.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return body

        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError

        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
    }
    override suspend fun getAll() {
        try {
            saveOnServerCheck() //проверка текущий локальной БД на незаписанные посты на сервер, если такие есть то они пытаются отправится на сервер через save()
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                responseErrMess = Pair(response.code(), response.message())
                throw ApiError(response.code(), response.message())
            }
            val bodyRsponse = response.body() ?: throw ApiError(response.code(), response.message())
            val entityList = bodyRsponse.toEntity() //Превращаем ответ в лист с энтити

            dao.insert(entityList)// А вот здесь в Локальную БД вставляем из сети все посты
            //А тут всем постам пришедшим с сервера ставим отметку тру
            for (postEntity: PostEntity in entityList)
            {
                if (!postEntity.savedOnServer) {
                    dao.saveOnServerSwitch(postEntity.id)
                }
            }

        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError

        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
//        try {
//            val response = PostsApi.service.getAll()
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(body.toEntity())
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
    }

//    override fun shareByIdAsync(
//        id: Long,
//        callback: PostRepository.RepositoryCallback<Post>
//    ) {
//        PostsApi.retrofitService.shareById(id).enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (!response.isSuccessful) {
//                    callback.onError(RuntimeException(response.message()))
//                    //return
//                }
//                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                callback.onError(Exception(t))
//            }
//        })
//    }
override fun getNewerCount(id: Long): Flow<Int> =flow {
    while (true) {
        delay(10_000L)
        val response = apiService.getNewer(id)
        if (!response.isSuccessful) {
            responseErrMess = Pair(response.code(), response.message())
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(body.toEntity().map { it.copy(savedOnServer = true, hidden = true) })//вставляем в базу, скопированный ответ с сервера с нужными нам маркерами, записан на сервере и не показывать.
        emit(body.size)
    }
}
.catch { throw UnknownError } //Репозиторий может выбрасывать исключения, но их тогда нужно обрабатывать во вьюмодели, тоже в кэтче флоу
//        .flowOn(Dispatchers.Default)




//    while (true) {
//        delay(10_000L)
//        val response = PostsApi.service.getNewer(id)
//        if (!response.isSuccessful) {
//            responseErrMess = Pair(response.code(), response.message())
//            throw ApiError(response.code(), response.message())
//        }
//
//        val body = response.body() ?: throw ApiError(response.code(), response.message())
//        dao.insert(body.toEntity(hidden = true))
//        emit(body.size)
//    }
//}
//.catch { e -> throw AppError.from(e) }
////.flowOn(Dispatchers.Default)


    suspend fun saveOnServerCheck() {
        try {
            for (postEntentety: PostEntity in dao.getAll()
                .asLiveData(Dispatchers.Default)
                .value?: emptyList()) {
                if (!postEntentety.savedOnServer) {
                    save(postEntentety.toDto())
                }
            }
        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError
        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
    }
    override suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel) {
        try {

            val mediaResponse = saveMedia(photoModel.file)
            if (!mediaResponse.isSuccessful) {
                responseErrMess = Pair(mediaResponse.code(), mediaResponse.message())
                throw ApiError(mediaResponse.code(), mediaResponse.message())
            }
            val media = mediaResponse.body() ?: throw ApiError(
                mediaResponse.code(),
                mediaResponse.message()
            )

//            val postEntentety = PostEntity.fromDto(post)
//            dao.insert(postEntentety)

            val response = apiService.save(
                post.copy(

                    attachment = Attachment(
                        media.id,
                        AttachmentType.IMAGE
                    )
                )
            )

            if (!response.isSuccessful) {
                responseErrMess = Pair(response.code(), response.message())
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.saveOnServerSwitch(body.id)

        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError
        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
        getAll()

    }
    override suspend fun save(post: Post) {
        try {
            val postEntentety = PostEntity.fromDto(post)
            dao.insert(postEntentety) //при сохранении поста, в базу вносится интентети с отметкой что оно не сохарнено на сервере
            val response = apiService.save(post.copy(id = 0)) //Если у поста айди 0 то сервер воспринимает его как новый
            if (!response.isSuccessful) { //если отвтет с сервера не пришел, то отметка о не записи на сервер по прежнему фолс
                responseErrMess = Pair(response.code(), response.message())
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.saveOnServerSwitch(body.id)// исключение не брошено меняем отметку о записи на сервере на тру

        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError
        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
        getAll()
    }
//        try {
//            val response = PostsApi.service.save(post)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body))
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }

    override suspend fun likeById(id: Long, likedByMe: Boolean) {
        try {
            dao.likeById(id)
            val response =
                apiService.let { if (likedByMe) it.dislikeById(id) else it.likeById(id) }
            if (!response.isSuccessful) {
                responseErrMess = Pair(response.code(), response.message())
                throw ApiError(response.code(), response.message())
            }
            response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError

        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }
//        try {
//            val response = PostsApi.service.likeById(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.likeById(id) //insert(PostEntity.fromDto(body))
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
    }

    override suspend  fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            responseErrMess = Pair(NetworkError.code.toInt(), NetworkError.message.toString())
            throw NetworkError

        } catch (e: Exception) {
            responseErrMess = Pair(UnknownError.code.toInt(), UnknownError.message.toString())
            throw UnknownError
        }

    }
}