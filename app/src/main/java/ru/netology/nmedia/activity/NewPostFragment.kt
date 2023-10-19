package ru.netology.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.text: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()
        val  postContent = arguments?.text

        binding.edit.requestFocus()

        if (postContent!=null){
            binding.contentEdit.text = postContent
            binding.edit.setText(postContent)
            binding.titleAdd.visibility = View.INVISIBLE
            binding.editImg.visibility = View.VISIBLE
        } else{
            binding.titleAdd.visibility = View.VISIBLE
            binding.contentEdit.visibility = View.INVISIBLE
            binding.editImg.visibility = View.INVISIBLE
        }

        binding.ok.setOnClickListener {
            if (!binding.edit.text.isNullOrBlank()) {
                val content = binding.edit.text.toString()
                viewModel.changeContentAndSave(content)
            }
            findNavController().navigateUp()
        }
        return binding.root
    }

}