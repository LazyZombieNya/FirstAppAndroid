package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.NewerCount
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

interface ApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count")count:Int): Response<List<Post>>

    @GET("posts/{id}/newer-count")
    suspend fun getNewer(@Path("id") id: Long): Response<NewerCount>


    @GET("posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long,@Query("count")count:Int): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long,@Query("count")count:Int): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>
    @Multipart
    @POST("media")
    suspend fun saveMedia(@Part part: MultipartBody.Part):Response<Media>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken):Response<Unit>
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): Response<Token>
}


