package ru.netology.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class DetailsFragmentPost : Fragment() {
    companion object {
        var Bundle.text: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()
        val  postContent = arguments?.text

        binding.editContent.text=postContent
        return binding.root
    }

}