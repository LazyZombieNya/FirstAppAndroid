package ru.netology.nmedia.repository
import androidx.lifecycle.*
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class  PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)
    private var responseErrMess: Pair<Int, String> = Pair(0, "")
    override fun getErrMess(): Pair<Int, String> {
        return responseErrMess
    }

    override suspend fun getAll() {
        try {
            saveOnServerCheck() //проверка текущий локальной БД на незаписанные посты на сервер, если такие есть то они пытаются отправится на сервер через save()
            val response = PostsApi.service.getAll()
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

    suspend fun saveOnServerCheck() {
        try {
            for (postEntentety: PostEntity in dao.getAll().value?: emptyList()) {
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
    override suspend fun save(post: Post) {
        try {
            val postEntentety = PostEntity.fromDto(post)
            dao.insert(postEntentety) //при сохранении поста, в базу вносится интентети с отметкой что оно не сохарнено на сервере
            val response = PostsApi.service.save(post.copy(id = 0)) //Если у поста айди 0 то сервер воспринимает его как новый
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
                PostsApi.service.let { if (likedByMe) it.dislikeById(id) else it.likeById(id) }
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
            val response = PostsApi.service.removeById(id)
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