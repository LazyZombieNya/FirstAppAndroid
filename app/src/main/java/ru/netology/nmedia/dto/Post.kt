package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    var likedByMe: Boolean=false,
    var likes: Int

) {
}