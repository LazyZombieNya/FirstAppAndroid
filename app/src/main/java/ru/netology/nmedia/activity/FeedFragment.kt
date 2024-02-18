package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragmentPost.Companion.id
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
        )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val actionMessage: String


        binding.swiperefresh.setOnRefreshListener {
            binding.swiperefresh.isRefreshing = false
            viewModel.loadPosts()
            //context?.toast("Update" )
        }

//        viewModel.toast.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            when (PostViewModel.ErrorAction.valueOf(it)) {
                PostViewModel.ErrorAction.LOAD_POST_ERROR -> {
                    Toast.makeText(context, getString(R.string.error_loading), Toast.LENGTH_LONG)
                        .show()

                }

                PostViewModel.ErrorAction.SAVE_ERROR -> {
                    Toast.makeText(context, getString(R.string.error_save_post), Toast.LENGTH_LONG)
                        .show()

                }

                PostViewModel.ErrorAction.REMOVE_ERROR -> {
                    Toast.makeText(
                        context,
                        getString(R.string.error_remove_post),
                        Toast.LENGTH_LONG
                    ).show()

                }

                PostViewModel.ErrorAction.SHARE_ERROR -> {
                    Toast.makeText(context, getString(R.string.error_unlike), Toast.LENGTH_LONG)
                        .show()

                }

                PostViewModel.ErrorAction.LIKE_ERROR -> {
                    Toast.makeText(context, getString(R.string.error_like), Toast.LENGTH_LONG)
                        .show()

                }

                PostViewModel.ErrorAction.UNLIKE_ERROR -> {
                    Toast.makeText(context, getString(R.string.error_unlike), Toast.LENGTH_LONG)
                        .show()

                }
            }
        })


        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onRefresh() {}

            override fun like(post: Post) {
                viewModel.likeById(post.id, post.likedByMe)
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

            override fun image(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().also { it.id = post.id })

            }

            override fun clickPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_detailsFragmentPost,
                    Bundle().also { it.id = post.id })
            }

            override fun remove(post: Post) {

                viewModel.removeById(post.id)

            }

            override fun edit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().also { it.text = post.content })
                viewModel.edit(post)
            }

        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            binding.errorGroup.isVisible = state.error
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost = state.posts.size > adapter.currentList.size && adapter.itemCount>0
            adapter.submitList(state.posts){
                if (newPost){
                    binding.list.smoothScrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = state.empty
        }

        //автоматический скрол списка постов в начало после добавления
        adapter.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                 if (positionStart == 0){
                     binding.list.smoothScrollToPosition(0)
                     binding.readNewPosts.isVisible=false
                 }
            }
        })

        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            binding.readNewPosts.isVisible = state>0
            println(state)
        }

        binding.readNewPosts.setOnClickListener{
            viewModel.readAll()
        }

//        binding.swiperefresh.setOnRefreshListener {
//            viewModel.refreshPosts()
//        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.FAB.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }

}



