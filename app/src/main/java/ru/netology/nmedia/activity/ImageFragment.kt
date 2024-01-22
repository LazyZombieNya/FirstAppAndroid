package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragmentPost.Companion.id
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.databinding.FragmentImagePostBinding
import ru.netology.nmedia.util.NiceNumberDisplay
import ru.netology.nmedia.viewmodel.PostViewModel

class ImageFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImagePostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()
        val id: Long? = arguments?.id

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.posts.find { it.id == id } ?: run {
                //findNavController().navigateUp()
                return@observe

            }

            val image = post.attachment?.url
            val url = "http://10.0.2.2:9999/media/${image}"
            Glide.with(binding.image)
                .load(url)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.image)

            binding.forwardBtn.setOnClickListener {
                findNavController().navigate(
                    R.id.action_imageFragment_to_feedFragment,
                    Bundle().also { it.text = post.content })
            }
            binding.like.isChecked = post.likedByMe
            binding.like.text = NiceNumberDisplay.shortNumber(post.likes)
            binding.share.text = NiceNumberDisplay.shortNumber(post.shares)
            binding.countView.text = NiceNumberDisplay.shortNumber(post.views)

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

            binding.like.setOnClickListener {
                viewModel.likeById(post.id, post.likedByMe)
                binding.like.text = NiceNumberDisplay.shortNumber(post.likes)
            }
            //для обработки события системной кнопки «Назад»
            val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                findNavController().navigate(
                    R.id.action_detailsFragmentPost_to_feedFragment)

            }
        }
        return binding.root

    }
}