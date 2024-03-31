package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.NiceNumberDisplay
import ru.netology.nmedia.view.load

interface OnInteractionListener {
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun onShare(post: Post)
    fun video(post: Post)
    fun image(post: Post)
    fun clickPost(post: Post)
    fun onRefresh()
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            else -> error("unknown item type")

        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)){
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }

    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            val avatarName = post.authorAvatar
            val url = "${BuildConfig.BASE_URL}/avatars/${avatarName}"
            Glide.with(binding.avatar)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.avatar)

            author.text = post.author
            published.text = post.published.toString()

            editContent.text = post.content
            countView.text = NiceNumberDisplay.shortNumber(post.views)
            if (!post.video.isNullOrBlank()) {
                videoContent.visibility = View.VISIBLE
            } else {
                videoContent.visibility = View.GONE
            }
            if (post.attachment != null) {
                binding.attachmentImage.visibility = View.VISIBLE
                val attachmentUrl = post.attachment.url
                val url = "http://10.0.2.2:9999/media/${attachmentUrl}"
                Glide.with(binding.attachmentImage)
                    .load(url)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(binding.attachmentImage)
            } else {
                binding.attachmentImage.visibility = View.GONE
            }

            like.isChecked = post.likedByMe
            like.text = NiceNumberDisplay.shortNumber(post.likes)
            share.text = NiceNumberDisplay.shortNumber(post.shares)

            videoContent?.setOnClickListener {
                onInteractionListener.video(post)
            }
            attachmentImage?.setOnClickListener {
                onInteractionListener.image(post)
            }
            //чтобы ссылки не мешали когда кликаешь на editContent
//            val rootClickListener = View.OnClickListener {
//                //onInteractionListener.clickPost(post)
//            }
//            root.setOnClickListener(rootClickListener)
//            editContent.setOnClickListener(rootClickListener)
            root?.setOnClickListener {
                onInteractionListener.clickPost(post)
            }

            like?.setOnClickListener {
                onInteractionListener.like(post)
            }
            share?.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
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

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}