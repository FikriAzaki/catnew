package com.capstoneproject.purrsonalcatapp.data.repository

import android.util.Log
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.remote.response.Answer
import com.capstoneproject.purrsonalcatapp.data.remote.response.DataPrediction
import com.capstoneproject.purrsonalcatapp.data.remote.response.DiseasePredictionResponse
import com.capstoneproject.purrsonalcatapp.data.remote.response.SymptomsResponse
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiService
import com.capstoneproject.purrsonalcatapp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PredictionRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    suspend fun getDiseasePrediction(symptoms: Map<String, Int>): Flow<Result<DiseasePredictionResponse>> =
        flow {
            val token = authPreferences.authToken.first()
            try {
                val answer = Answer(symptoms)
                val response = ApiConfig.getApiService(token).getDiseasePrediction(answer)
                emit(Result.Success(response))
                Log.d("RESPONSE DISEASE", "result: ${response.data?.predictions}")
            } catch (e: Exception) {
                emit(Result.Error(e.toString()))
                Log.e("GET DISEASE ERROR", "${e.message}")
            }
        }

    suspend fun getSymptomsCat(): Flow<Result<SymptomsResponse>> = flow {
        try {
            val token = authPreferences.authToken.first()
            val response = ApiConfig.getApiService(token).getSymptoms()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

}
