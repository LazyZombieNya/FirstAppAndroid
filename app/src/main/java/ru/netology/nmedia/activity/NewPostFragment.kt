package ru.netology.nmedia.activity


import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory


class NewPostFragment : Fragment() {
    companion object {
        var Bundle.text: String? by StringArg
    }
    private val dependencyContainer = DependencyContainer.getInstance()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNewPostBinding.inflate(layoutInflater)
        val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment,
            factoryProducer = { ViewModelFactory(dependencyContainer.repository,dependencyContainer.appAuth) })

        val postContent = arguments?.text


        val photoResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val uri = it.data?.data ?:return@registerForActivityResult
                val file = uri.toFile()
                viewModel.setPhoto(uri,file)
            }

        }

        if (postContent != null) {
            binding.contentEdit.text = postContent
            binding.edit.setText(postContent)
            binding.titleAdd.visibility = View.INVISIBLE
            binding.editImg.visibility = View.VISIBLE
        } else {
            val sPref = activity?.getSharedPreferences("MyPref", MODE_PRIVATE)
            val savedText: String? = sPref?.getString("SAVED_TEXT", "")
            binding.edit.setText(savedText)
            binding.titleAdd.visibility = View.VISIBLE
            binding.contentEdit.visibility= View.GONE
            binding.editImg.visibility = View.GONE
        }
        viewModel.photo.observe(viewLifecycleOwner){
            if(it == null){
                binding.imageContainer.isGone=true
                return@observe
            }
            binding.imageContainer.isVisible =true
            binding.preview.setImageURI(it.uri)
        }
        binding.edit.requestFocus()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        val content = binding.edit.text.toString()
                        viewModel.changeContent(content)
                        viewModel.save()
                        activity?.deleteSharedPreferences("MyPref")//Удаляет сохраннее данные из черновика
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner)

        binding.remove.setOnClickListener{
            viewModel.clearPhoto()
        }
//        binding.ok.setOnClickListener {
//            if (!binding.edit.text.isNullOrBlank()) {
//                val content = binding.edit.text.toString()
//                viewModel.changeContent(content)
//                viewModel.save()
//                activity?.deleteSharedPreferences("MyPref")//Удаляет сохраннее данные из черновика
//            }
//            findNavController().navigateUp()
//        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        //для обработки события системной кнопки «Назад»
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.edit.text != null) {
                val sPref =
                    activity?.getSharedPreferences("MyPref", MODE_PRIVATE) ?: return@addCallback
                with(sPref.edit()) {
                    putString("SAVED_TEXT", binding.edit.getText().toString())
                    apply()
                }
            }
            findNavController().navigate(
                R.id.action_newPostFragment_to_feedFragment
            )
        }
        binding.takePhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(2048,2048)
                .createIntent( photoResultContract::launch)
        }
        binding.pickPhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(2048,2048)
                .createIntent( photoResultContract::launch)
        }



        return binding.root
    }

}