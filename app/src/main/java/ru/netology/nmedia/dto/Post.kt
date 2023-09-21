package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean=false,
    val likes: Int,
    val shares:Int,
    val views:Int,
    val video:String
) {
}