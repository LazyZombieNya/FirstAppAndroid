package ru.netology.nmedia.entity//package ru.netology.nmedia.entity
//
//
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import ru.netology.nmedia.dto.Attachment
//import ru.netology.nmedia.dto.Post
//
//
//@Entity
//
//data class PostEntity (
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
//    @ColumnInfo(name = "author", defaultValue = "Me")
//    val author: String ,
//    @ColumnInfo(name = "authorAvatar", defaultValue = "netology.jpg")
//    val authorAvatar:String,
//    @ColumnInfo(name = "published", defaultValue = "now")
//    val published: String,
//    val content: String,
//    @ColumnInfo(name = "likedByMe", defaultValue = "0")
//    val likedByMe: Boolean,
//    @ColumnInfo(name = "likes", defaultValue = "0")
//    val likes: Int,
//    @ColumnInfo(name = "shares", defaultValue = "0")
//    val shares:Int,
//    @ColumnInfo(name = "views", defaultValue = "0")
//    val views:Int,
//    @ColumnInfo(name = "video", defaultValue = "")
//    val video:String,
//    @ColumnInfo(name = "attachment", defaultValue = "")
//    val attachment: Attachment
//){
////    fun toDto() = Post(id, author,authorAvatar,published, content , likedByMe, likes,shares,views,video,attachment)
////
////    companion object {
////        fun fromDto(dto: Post) =
////            PostEntity(dto.id, dto.author,dto.authorAvatar, dto.published, dto.content, dto.likedByMe, dto.likes,dto.shares,dto.views,dto.video,dto.attachment)
////
////    }
//}