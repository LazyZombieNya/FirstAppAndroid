package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "1 мая в 18:36",
            likedByMe = false,
            likes = 999,
            shares = 999999,
            views = 9999999
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            countLike.text = shortNumber(post.likes)
            countShare.text = shortNumber(post.shares)
            countView.text = shortNumber(post.views)
            if (post.likedByMe) {
                like?.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
            like?.setOnClickListener {
                println("clicked like")
                post.likedByMe = !post.likedByMe
                if (post.likedByMe) post.likes++ else post.likes--

                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24
                    else R.drawable.ic_baseline_favorite_border_24
                )
                countLike.text = shortNumber(post.likes)
            }
            share?.setOnClickListener {
                post.shares++
                countShare.text = shortNumber(post.shares)
            }
            root?.setOnClickListener {
                println("clicked root")
            }
            avatar?.setOnClickListener {
                println("clicked avatar")
            }
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

