package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar ="",
    content = "",
    published = 0,
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    video = null,
    attachment = null
)
@HiltViewModel
//@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

   // private val _data = MutableLiveData(FeedModel())

    val data: Flow<PagingData<Post>> = appAuth
        .authState
        .flatMapLatest {auth ->
            repository.data.map { posts->
                    posts.map { it.copy(ownedByMe = auth.id == it.authorId) }
            }
        }.flowOn(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { _dataState.postValue(FeedModelState(error=true)) }
//            .asLiveData(Dispatchers.Default,100)
//    }

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo:LiveData<PhotoModel?>
        get()= _photo

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    init {
        loadPosts()
    }

    val toast : LiveData<String>
        get() = toastLiveData

    private val toastLiveData = SingleLiveEvent<String>()

    fun readAll(){
        viewModelScope.launch {
            repository.readAllPost()
        }
    }
    fun toastErrorMsg(msg: String) {
        toastLiveData.value = msg
    }

    init {
        loadPosts()
    }

    fun setPhoto(uri:Uri, file: File){
        _photo.value = PhotoModel(uri, file)
    }
    enum class ErrorAction {
        LOAD_POST_ERROR,
        LIKE_ERROR,
        UNLIKE_ERROR,
        SAVE_ERROR,
        REMOVE_ERROR,
        SHARE_ERROR
    }
    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
            toastErrorMsg("LOAD_POST_ERROR")//R.string.error_loading))
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    val photoModel = _photo.value
                    if (photoModel == null) {
                        repository.save(it)
                    }else{
                        repository.saveWithAttachment(it,photoModel)
                    }
                    _dataState.value = FeedModelState()
                    _postCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                    toastErrorMsg("SAVE_ERROR")//R.string.error_save_post
                }

            }
        }
        edited.value = empty
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)

    }



    fun likeById(id: Long, likedByMe: Boolean) {
              viewModelScope.launch {
                try {
                    repository.likeById(id,likedByMe)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                    toastErrorMsg("SAVE_ERROR")//R.string.error_save_post
                }

//        val old = _data.value?.posts.orEmpty().asReversed()
//        val oldPost = old.find { it.id == id } ?: return
//        val post = if (oldPost.likedByMe) {
//            repository.unLikeByIdAsync(id,  object : PostRepository.RepositoryCallback<Post> {
//                override fun onSuccess(result: Post) {
//                    _data.postValue(
//                        _data.value?.copy(posts = _data.value?.posts.orEmpty().map { if (it.id != id) it else result }))
//                }
//                override fun onError(e: Exception) {
//                    _data.postValue(_data.value?.copy(posts = old))
//                    toastErrorMsg("UNLIKE_ERROR")//R.string.error_unlike
//                }
//            })
//        } else {
//            repository.likeByIdAsync(id, object : PostRepository.RepositoryCallback<Post> {
//                override fun onSuccess(result: Post) {
//                    _data.postValue(
//                        _data.value?.copy(posts = _data.value?.posts.orEmpty().map { if (it.id != id) it else result }))
//                }
//
//                override fun onError(e: Exception) {
//                    _data.postValue(_data.value?.copy(posts = old))
//                    toastErrorMsg("LIKE_ERROR")//R.string.error_like
//                }
//            })
//        }
    }
    }

    fun getById(id: Long): Post? {
        var post: Post? = null
        viewModelScope.launch {
            try {
                post = repository.getById(id)
            } catch (e: Exception) {
                toastErrorMsg("REMOVE_ERROR")
                _dataState.value = FeedModelState(error = true)
            }
        }
        return post
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                toastErrorMsg("REMOVE_ERROR")
                _dataState.value = FeedModelState(error = true)
            }
        }
//        val old = _data.value?.posts.orEmpty()
//        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
//            override fun onSuccess(result: Unit) {
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//            override fun onError(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//                toastErrorMsg("REMOVE_ERROR")//R.string.error_remove_post
//            }
//        })
    }

    fun shareById(id: Long) {

//            val old = _data.value?.posts.orEmpty()
//            try {
//                val post = repository.shareByIdAsync(id)
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty().map {
//                        if (it.id != id) it
//                        else it.copy(shares = it.shares + 1)
//                    })
//                )
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun clearPhoto() {
        _photo.value = null
    }

}