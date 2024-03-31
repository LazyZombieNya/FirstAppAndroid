package ru.netology.nmedia.dto

sealed interface FeedItem{
    val id:Long
}

data class Post(
    override val id: Long,
    val authorId:Long,
    val author: String,
    val authorAvatar:String,
    val published: Long,
    val content: String,
    val likedByMe: Boolean=false,
    val likes: Int,
    val shares:Int,
    val views:Int,
    val video: String?,
    val attachment: Attachment? =null,
    val ownedByMe:Boolean = false
):FeedItem

data class Ad(
    override val id: Long,
    val image:String,
):FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType,
)