package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(): Flow<Long>
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun getById(id: Long): Post
    suspend fun likeById(id: Long, likedByMe: Boolean)
    fun getErrMess(): Pair<Int, String>
    suspend fun readAllPost()
    suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel)
    suspend fun requestToken(login: String, password: String): Token
}