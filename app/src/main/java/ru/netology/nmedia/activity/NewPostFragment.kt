package ru.netology.nmedia.activity


import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {
    companion object {
        var Bundle.text: String? by StringArg
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            val sPref = activity?.getSharedPreferences("MyPref", MODE_PRIVATE)
            val savedText: String? = sPref?.getString("SAVED_TEXT", "")
            binding.edit.setText(savedText)
            binding.titleAdd.visibility = View.VISIBLE
            binding.contentEdit.visibility = View.INVISIBLE
            binding.editImg.visibility = View.INVISIBLE
        }

        binding.ok.setOnClickListener {
            if (!binding.edit.text.isNullOrBlank()) {
                val content = binding.edit.text.toString()
                viewModel.changeContent(content)
                viewModel.save()
                activity?.deleteSharedPreferences("MyPref")//Удаляет сохраннее данные из черновика
            }
            findNavController().navigateUp()
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        //для обработки события системной кнопки «Назад»
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.edit.text != null) {
                val sPref = activity?.getSharedPreferences("MyPref", MODE_PRIVATE)?: return@addCallback
                with (sPref.edit() ){
                    putString("SAVED_TEXT", binding.edit.getText().toString())
                    apply()
                }
            }
            findNavController().navigate(
                R.id.action_newPostFragment_to_feedFragment)
        }
        viewModel.postCreated.observe(viewLifecycleOwner){
            findNavController().navigateUp()
        }


        return binding.root
    }


}