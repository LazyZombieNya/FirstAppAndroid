package ru.netology.nmedia.viewmodel

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class SignInViewModel (application: Application) : AndroidViewModel(application) {

    private val _auth = SingleLiveEvent<Unit>()
    val auth: SingleLiveEvent<Unit>
        get() = _auth

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    fun sendRequest(login:String, password:String){

        viewModelScope.launch {
            try {
                val token: Token = repository.requestToken(login, password)
                AppAuth.getInstance().setAuth(token.id, token.token)
                _auth.value = Unit
                println("Token: "+token)
            } catch (e: Exception) {
            }
        }

    }

}