package ru.netology.nmedia.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.NiceNumberDisplay
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
            binding.author.text = post.author
            binding.published.text = post.published
            binding.editContent.text = post.content
            binding.like.isChecked = post.likedByMe

            if (post.video.toString()!="0"){
                binding.videoContent.visibility = View.VISIBLE
            } else {
                binding.videoContent.visibility = View.GONE
            }
            binding.like.text = NiceNumberDisplay.shortNumber(post.likes)
            binding.share.text = NiceNumberDisplay.shortNumber(post.shares)
            binding.countView.text = NiceNumberDisplay.shortNumber(post.views)

            binding.edit.setOnClickListener {
                findNavController().navigate(
                    R.id.action_detailsFragmentPost_to_newPostFragment,
                    Bundle().also { it.text = post.content })
                viewModel.edit(post)
            }

            binding.like.setOnClickListener {
                if (id != null) {
                    viewModel.likeById(id)
                }
            }
            binding.share.setOnClickListener {
                if (id != null) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.post_share))
                    startActivity(shareIntent)
                    viewModel.shareById(id)
                }
            }
            binding.delete.setOnClickListener {
                if (id != null) {
                    viewModel.removeById(id)
                    findNavController().navigate(
                        R.id.action_detailsFragmentPost_to_feedFragment)
                }
            }


            binding.forward.setOnClickListener {
                findNavController().navigate(
                    R.id.action_detailsFragmentPost_to_feedFragment)
            }
        }
        //для обработки события системной кнопки «Назад»
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(
                R.id.action_detailsFragmentPost_to_feedFragment)

        }


        return binding.root
    }

}