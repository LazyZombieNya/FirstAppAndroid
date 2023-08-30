package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

typealias OnLikeListener = (post:Post) ->Unit
typealias OnShareListener = (post:Post) ->Unit
class PostAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
):ListAdapter<Post,PostViewHolder>(PostDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostViewHolder(binding,onLikeListener,onShareListener)
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}
class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
):RecyclerView.ViewHolder(binding.root){
    fun bind(post: Post){
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            countLike.text = shortNumber(post.likes)
            countShare.text = shortNumber(post.shares)
            countView.text = shortNumber(post.views)
            like?.setImageResource(if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24)

            like?.setOnClickListener {
                onLikeListener(post)
            }
            share?.setOnClickListener {
                onShareListener(post)
            }
        }
    }
    private fun shortNumber(number: Int): String {
        return when (number) {
            in 0..999 -> number.toString()
            in 1000..1099 -> "1K"
            in 1100..9999 -> (number / 1000).toString() + "." + ((number % 1000) / 100).toString() + "K"
            in 10000..999999 -> (number / 1000).toString() + "K"
            in 1000000..1099999 -> (number / 1000000).toString() + "M"
            in 1100000..9999999 -> (number / 1000000).toString() + "." + ((number % 1000000) / 100000).toString() + "M"
            else -> "Many"
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