package ru.netology.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class DetailsFragmentPost : Fragment() {
    companion object {
        var Bundle.id: Long? by LongArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()
        val id: Long? = arguments?.id

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == id } ?: run {
                //findNavController().navigateUp()
                return@observe

            }
            binding.author.setText(post.author)
            binding.published.setText(post.published)
            binding.editContent.text = post.content
            if (post.video.isNotEmpty())
                binding.videoContent.visibility = View.VISIBLE else binding.videoContent.visibility = View.INVISIBLE
            binding.like.text=post.likes.toString()
            binding.share.text=post.shares.toString()
            binding.countView.text=post.views.toString()
        }

        binding.edit.setOnClickListener{

            //findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,Bundle().also { it.id=id)

        }

        return binding.root
    }

}