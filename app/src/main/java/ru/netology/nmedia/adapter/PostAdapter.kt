package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.NiceNumberDisplay

interface OnInteractionListener{
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun onShare(post: Post)
    fun video(post: Post)
    fun clickPost(post: Post)
    fun onRefresh()
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
):ListAdapter<Post,PostViewHolder>(PostDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostViewHolder(binding,onInteractionListener)

    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
):RecyclerView.ViewHolder(binding.root){
    fun bind(post: Post){
        binding.apply {
            val avatarName = post.authorAvatar
            val url = "http://10.0.2.2:9999/avatars/${avatarName}"
            Glide.with(binding.avatar)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.avatar)

            author.text = post.author
            published.text = post.published

            editContent.text = post.content
            countView.text = NiceNumberDisplay.shortNumber(post.views)
            if (!post.video.isNullOrBlank()) {
                videoContent.visibility = View.VISIBLE
            } else {
                videoContent.visibility = View.GONE
            }
//            if (!post.attachment.isNullOrBlank()){
//                binding.attachmentImage.visibility = View.VISIBLE
//                val attachmentName = post.attachment
//                val url = "http://10.0.2.2:9999/images/${attachmentName}"
//                Glide.with(binding.attachmentImage)
//                    .load(url)
//                    .circleCrop()
//                    .placeholder(R.drawable.ic_loading_100dp)
//                    .error(R.drawable.ic_error_100dp)
//                    .timeout(10_000)
//                    .into(binding.attachmentImage)
//            } else {
//                binding.attachmentImage.visibility = View.GONE
//            }

            like.isChecked = post.likedByMe
            like.text = NiceNumberDisplay.shortNumber(post.likes)
            share.text = NiceNumberDisplay.shortNumber(post.shares)

            videoContent?.setOnClickListener {
                onInteractionListener.video(post)
            }
            //чтобы ссылки не мешали когда кликаешь на editContent
            val rootClickListener = View.OnClickListener {
                onInteractionListener.clickPost(post)
            }
            root.setOnClickListener(rootClickListener)
            editContent.setOnClickListener(rootClickListener)
//            root?.setOnClickListener {
//                onInteractionListener.clickPost(post)
//            }

            like?.setOnClickListener {
                onInteractionListener.like(post)
            }
            share?.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context,it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

}
class PostDiffCallback : DiffUtil.ItemCallback<Post>(){
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem==newItem
    }
}