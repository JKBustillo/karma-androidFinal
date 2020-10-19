package com.jabustillo.karma.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.jabustillo.karma.repository.AuthRepository

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    fun logged() = repository.logged // get if there's a user logged

    fun register(name: String, email: String, password : String, func: Unit){
        repository.register(name, email, password, func)
    }

    fun login(email: String, password : String, context: Context){
        repository.login(email,password, context)
    }

    fun logOut(){
        repository.logOut()
    }
}