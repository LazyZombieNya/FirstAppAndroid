package ru.netology.nmedia.repository

import  androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import java.util.Calendar

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L

    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 33,
            shares = 25,
            views = 5
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 999,
            shares = 999999,
            views = 4
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 999,
            shares = 99999,
            views = 3
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 9999,
            shares = 9999,
            views = 2
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 2,
            shares = 9,
            views = 1
        ),

    ).reversed()

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