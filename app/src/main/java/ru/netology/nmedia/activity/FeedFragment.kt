package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragmentPost.Companion.id
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()


        val adapter = PostAdapter(object: OnInteractionListener{
            override fun like(post: Post) {
                viewModel.likeById(post.id)
            }
            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.post_share))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }
            override fun video(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(post.video)
                }
                val playVideo = Intent.createChooser(intent, "play Video")
                startActivity(playVideo)

            }

            override fun clickPost(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_detailsFragmentPost,Bundle().also {  it.text = post.content })
            }
            override fun remove(post: Post) {

                viewModel.removeById(post.id)

            }

            override fun edit(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,Bundle().also { it.text = post.content }) //TODO
                viewModel.edit(post)
            }

        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val newPost = posts.size>adapter.currentList.size
            adapter.submitList(posts)
            if (newPost) {
                binding.list.smoothScrollToPosition(0)
            }
        }
        binding.FAB.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }


        return binding.root
    }



}



