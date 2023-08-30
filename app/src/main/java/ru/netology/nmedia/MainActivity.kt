package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostAdapter(object: OnInteractionListener{
            override fun like(post: Post) {
                viewModel.likeById(post.id)
            }
            override fun share(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                viewModel.edit(post)
            }

        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size>adapter.currentList.size
            adapter.submitList(posts)
            if (newPost) {
                binding.list.smoothScrollToPosition(0)
            }
        }
        binding.closeEdit.setOnClickListener{
            binding.groupEdit.visibility = View.GONE // перестаёт занимать место на экране
            binding.groupEdit.visibility = View.INVISIBLE // невидима, но занимает место на экране
            binding.editContent.setText("")
            binding.editContent.clearFocus()
            AndroidUtils.hideKeyboard(it)
        }

        viewModel.edited.observe(this){
            if (it.id!=0L){
                binding.editContent.setText(it.content)
                binding.editContent.focusAndShowKeyboard()
                binding.editMessage.text=it.content
                binding.groupEdit.visibility = View.VISIBLE
            }
        }

        binding.btnSave.setOnClickListener{
            val text =binding.editContent.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_content, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContentAndSave(text)
            binding.editContent.setText("")
            binding.editContent.clearFocus()
            AndroidUtils.hideKeyboard(it)
            binding.groupEdit.visibility = View.GONE // перестаёт занимать место на экране
            binding.groupEdit.visibility = View.INVISIBLE // невидима, но занимает место на экране
        }
    }
}



