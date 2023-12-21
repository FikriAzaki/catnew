package com.capstoneproject.purrsonalcatapp.data.repository

import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.remote.response.LoginResponse
import com.capstoneproject.purrsonalcatapp.data.remote.response.RegisterResponse
import com.capstoneproject.purrsonalcatapp.data.remote.response.UserResponse
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    suspend fun registerAuth(username: String, email: String, password: String): RegisterResponse {
        return withContext(Dispatchers.IO) {
            try {

                val response = apiService.registerUser(username, email, password)

                if (!response.error!!) {
                    response
                } else {
                    RegisterResponse(data = null, error = true, message = "Register Failed")
                }
            } catch (e: Exception) {
                RegisterResponse(error = true, message = "An error occurred: ${e.message}")
            }
        }
    }

    suspend fun loginAuth(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginUser(email, password)
                if (!response.error!!) {
                    response
                } else {
                    LoginResponse(error = true, message = "Login Failed")
                }
            } catch (e: Exception) {
                LoginResponse(error = true, message = "An error occurred ${e.message}")
            }
        }
    }

    suspend fun getUserData(): Flow<UserResponse> = flow {
        val token = authPreferences.authToken.first()

        try {
            val response = ApiConfig.getApiService(token).getUserData()
            emit(response)
        } catch (e: Exception) {
            // Handle errors appropriately, for example, emit an error state
            // You can customize this based on your error handling strategy
            emit(UserResponse(error = true, message = "${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
}