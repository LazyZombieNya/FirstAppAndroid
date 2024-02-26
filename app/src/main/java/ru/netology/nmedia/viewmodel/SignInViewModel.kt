package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth

) : ViewModel() {

    private val _auth = SingleLiveEvent<Unit>()
    val auth: SingleLiveEvent<Unit>
        get() = _auth

//    private val repository: PostRepository =
//        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    fun sendRequest(login:String, password:String){

        viewModelScope.launch {
            try {
                val token: Token = repository.requestToken(login, password)
                appAuth.setAuth(token.id, token.token)
                _auth.value = Unit
                println("Token: "+token)
            } catch (e: Exception) {
            }
        }

    }

}