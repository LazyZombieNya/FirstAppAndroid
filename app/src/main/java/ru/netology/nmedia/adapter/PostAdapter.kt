package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
interface OnInteractionListener{
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun share(post: Post) {

    }
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
            author.text = post.author
            published.text = post.published
            editContent.text = post.content
           //countLike.text = shortNumber(post.likes)
            //countShare.text = shortNumber(post.shares)
            countView.text = shortNumber(post.views)

            like.isChecked = post.likedByMe
            like.text = shortNumber(post.likes)
            share.text = shortNumber(post.shares)

            like?.setOnClickListener {
                onInteractionListener.like(post)
            }
            share?.setOnClickListener {
                onInteractionListener.share(post)
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