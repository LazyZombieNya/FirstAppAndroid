package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.res.Resources
import android.provider.Settings.Global.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.collections.List as List

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar ="",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    views = 0,
    video = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)


    val toast : LiveData<String>
        get() = toastLiveData

    private val toastLiveData = SingleLiveEvent<String>()

    fun toastErrorMsg(msg: String) {
        toastLiveData.value = msg
    }

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.RepositoryCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value=(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                toastErrorMsg("Something went wrong. Try again later")//R.string.error_loading
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveCallback{
                override fun onSuccess(value: Unit) {
                    _postCreated.value=(Unit)
                }

                override fun onError(e: Exception) {
                    _data.value=(FeedModel(error = true))
                    toastErrorMsg("Error saving post")//R.string.error_save_post
                }
            })

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


    fun likeById(id: Long) {
        val old = _data.value?.posts.orEmpty().asReversed()
        val oldPost = old.find { it.id == id } ?: return
        val post = if (oldPost.likedByMe) {
            repository.unLikeByIdAsync(id,  object : PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(result: Post) {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty().map { if (it.id != id) it else result }))
                }
                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(posts = old))
                    toastErrorMsg("Unlike error")//R.string.error_unlike
                }
            })
        } else {
            repository.likeByIdAsync(id, object : PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(result: Post) {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty().map { if (it.id != id) it else result }))
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(posts = old))
                    toastErrorMsg("Like  error")//R.string.error_like
                }
            })
        }
    }


    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess(result: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
                toastErrorMsg("Error deleting post")//R.string.error_remove_post
            }
        })
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