package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.SignInViewModel

class AuthFragment :Fragment(){
    private val viewModel: SignInViewModel by activityViewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthenticationBinding.inflate(layoutInflater)

       // binding.editLogin.requestFocus()
        binding.editLogin.focusAndShowKeyboard()

        viewModel.auth.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.signIn.setOnClickListener {
            val login: String = binding.editLogin.text.toString()
            val password: String = binding.editPassword.text.toString()
            if (login.isNotEmpty()) {
                viewModel.sendRequest(login, password)
            } else {
                Toast.makeText(context, R.string.empty_login, Toast.LENGTH_SHORT).show()
                binding.editLogin.focusAndShowKeyboard()
            }
        }
        return binding.root
    }
}