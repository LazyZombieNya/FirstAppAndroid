package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
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

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _data = MutableLiveData(FeedModel())


    val data: LiveData<FeedModel> =repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    init {
        loadPosts()
    }


    val toast : LiveData<String>
        get() = toastLiveData

    private val toastLiveData = SingleLiveEvent<String>()

    fun toastErrorMsg(msg: String) {
        toastLiveData.value = msg
    }

    init {
        loadPosts()
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
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
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

}