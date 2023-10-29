package ru.netology.nmedia.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post


@Entity

data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "author", defaultValue = "Me")
    val author: String ,
    @ColumnInfo(name = "published", defaultValue = "now")
    val published: String,
    val content: String,
    @ColumnInfo(name = "likedByMe", defaultValue = "0")
    val likedByMe: Boolean,
    @ColumnInfo(name = "likes", defaultValue = "0")
    val likes: Int,
    @ColumnInfo(name = "shares", defaultValue = "0")
    val shares:Int,
    @ColumnInfo(name = "views", defaultValue = "0")
    val views:Int,
    @ColumnInfo(name = "video", defaultValue = "")
    val video:String
){
    fun toDto() = Post(id, author,published, content , likedByMe, likes,shares,views,video)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.published, dto.content, dto.likedByMe, dto.likes,dto.shares,dto.views,dto.video)

    }
}