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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragmentPost.Companion.id
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.databinding.FragmentImagePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.NiceNumberDisplay
import ru.netology.nmedia.viewmodel.PostViewModel
@AndroidEntryPoint
class ImageFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImagePostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()
        //val id: Long? = arguments?.id

        setFragmentResultListener("requestIdForImageFragment") { key, bundle ->
            val result = bundle.getLong("id")

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val post =
                    (viewModel.data.single().toList().filter { it -> it.id == result }[0] as Post)!!
                        .copy()

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