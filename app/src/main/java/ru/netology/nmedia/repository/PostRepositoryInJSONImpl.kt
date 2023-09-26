package ru.netology.nmedia.repository

import android.content.Context
import  androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.util.Calendar

class PostRepositoryInJSONImpl(
        private val context: Context,
    ) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var nextId = 1L
    private var posts  = emptyList<Post>()
    private val postKey = "posts"
    private val nextIdKey = "next_id"
    private val data = MutableLiveData (posts)

    init {
        prefs.getString(postKey, null)?.let {
            posts = gson.fromJson(it, type)
        }
        prefs.getLong(nextIdKey, nextId)

        data.value = posts


    }

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id:Long){
        posts = posts.map { if(it.id != id)  it else it.copy(likedByMe = !it.likedByMe,likes = if(it.likedByMe) it.likes -1 else it.likes +1)}
        data.value = posts
        sync()
    }

    override fun shareById(id:Long) {
        posts = posts.map{if(it.id != id)  it else it.copy(shares=it.shares+1)}
        data.value = posts
        sync()
    }
    override fun removeById(id:Long){
        posts = posts.filter { it.id!=id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        posts = if(post.id==0L){
            listOf(post.copy(id = nextId++, author = "Me", published = (Calendar.getInstance().time).toString()))+ posts
        } else{
            posts.map{if(it.id != post.id)  it else it.copy(content =post.content)}
        }
        data.value = posts
        sync()
    }
    private fun sync() {
        with(prefs.edit()) {
            putString(postKey, gson.toJson(posts))
            putLong(nextIdKey,nextId)
            apply()
        }
    }
}