package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Token
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long, likedByMe: Boolean)
    fun getErrMess(): Pair<Int, String>
    suspend fun readAllPost()
    suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel)
    suspend fun requestToken(login: String, password: String): Token
}