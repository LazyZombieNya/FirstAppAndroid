package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.edit.requestFocus()
        val postContent: String? = intent.extras?.getString(Intent.EXTRA_TITLE)
        binding.edit.setText(postContent)
        if (postContent!=""){
            binding.contentEdit.setText(postContent)
            binding.titleAdd.visibility = View.INVISIBLE
            binding.editImg.visibility = View.VISIBLE
        } else{
            binding.titleAdd.visibility = View.VISIBLE
            binding.contentEdit.visibility = View.INVISIBLE
            binding.editImg.visibility = View.INVISIBLE
        }

        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}