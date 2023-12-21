package com.capstoneproject.purrsonalcatapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.remote.response.LoginResponse
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository, private val authPreferences: AuthPreferences): ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = authRepository.loginAuth(email, password)
            if(!response.error!!) {
                authPreferences.saveAuthToken(response.data!!.token)
            }
            _loginResult.value = response
        }
    }
}