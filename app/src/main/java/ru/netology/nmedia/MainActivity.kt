package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this){post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                countLike.text = shortNumber(post.likes)
                countShare.text = shortNumber(post.shares)
                countView.text = shortNumber(post.views)
                like?.setImageResource(if (post.likedByMe)R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24)
            }
        }
            binding.like?.setOnClickListener {
                viewModel.like()
            }
            binding.share?.setOnClickListener {
                viewModel.share()
            }

    }

    private fun shortNumber(number: Int):String {
        return when (number){
            in 0..999->number.toString()
            in 1000..1099->"1K"
            in 1100..9999->(number/1000).toString()+"."+((number%1000)/100).toString()+"K"
            in 10000..999999->(number/1000).toString()+"K"
            in 1000000..1099999->(number/1000000).toString()+"M"
            in 1100000..9999999->(number/1000000).toString()+"."+((number%1000000)/100000).toString()+"M"
            else->"Many"
        }
    }


}

