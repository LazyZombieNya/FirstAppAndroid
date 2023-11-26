package ru.netology.nmedia.repository
import ru.netology.nmedia.activity.FeedFragment
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: RepositoryCallback<List<Post>>)

    fun likeByIdAsync(id:Long,callback: RepositoryCallback<Post>)
    fun unLikeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun shareByIdAsync(id:Long,callback: RepositoryCallback<Post>)
    fun removeByIdAsync(id: Long, callback:RemoveCallback)
    fun saveAsync(post: Post,callback: SaveCallback)


    interface SaveCallback {
        fun onSuccess(value: Unit)
        fun onError(e: Exception) {}
    }
    interface RemoveCallback {
        fun onSuccess(result: Unit)
        fun onError(e: Exception) {}
    }
    interface RepositoryCallback<T> {
        fun onSuccess(result: T)
        fun onError(e: Exception) {}
    }
}