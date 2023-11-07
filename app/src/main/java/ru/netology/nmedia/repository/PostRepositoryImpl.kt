package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun likeById(id: Long):Post {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}posts/$id/likes/")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java) //преобразуем ответ в Post и возвращаем вьюмодели готовый экземпляр, которым нужно будет заменить предыдущий вариант (через маппинг по id)
            }

    }


    override fun shareById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}posts/$id/shares/")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java) //преобразуем ответ в Post и возвращаем вьюмодели готовый экземпляр, которым нужно будет заменить предыдущий вариант (через маппинг по id)
            }
    }

    override fun save(post: Post):Post {
        println("Like^ "+gson.toJson(post).toRequestBody(jsonType))
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, Post::class.java)
            }
//        val call = client.newCall(request)
//        val response = call.execute()
//        val responseString = response.body?.string()?: error("body is null")
//        println("Like^ "+request)
//        return gson.fromJson(responseString,Post::class.java)
    }
//    override fun save(post: Post) {
//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .execute()
//            .close()
//    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}