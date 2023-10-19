package ru.netology.nmedia.repository

import  androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import java.util.Calendar

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L

    private var posts  = emptyList<Post>()


    private val data = MutableLiveData (posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id:Long){
        posts = posts.map { if(it.id != id)  it else it.copy(likedByMe = !it.likedByMe,likes = if(it.likedByMe) it.likes -1 else it.likes +1)}
        data.value = posts
    }

    override fun shareById(id:Long) {
        posts = posts.map{if(it.id != id)  it else it.copy(shares=it.shares+1)}
        data.value = posts
    }
    override fun removeById(id:Long){
        posts = posts.filter { it.id!=id }
        data.value = posts
    }

    override fun save(post: Post) {
        posts = if(post.id==0L){
            listOf(post.copy(id = nextId++, author = "Me", published = (Calendar.getInstance().time).toString()))+ posts
        } else{
            posts.map{if(it.id != post.id)  it else it.copy(content =post.content)}
        }
        data.value = posts
    }
}