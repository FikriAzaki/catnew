package com.capstoneproject.purrsonalcatapp.ui.DiseasePrediction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.purrsonalcatapp.data.remote.response.DiseasePredictionResponse
import com.capstoneproject.purrsonalcatapp.data.remote.response.SymptomsResponse
import com.capstoneproject.purrsonalcatapp.data.repository.PredictionRepository
import com.capstoneproject.purrsonalcatapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DiseasePredictionViewModel(private val predictionRepository: PredictionRepository) :
    ViewModel() {

    private var isQuestionAnswered = false
    private val symptomsInput = mutableListOf<Pair<String, Int>>()

    fun addSymptom(symptom: String, answer: Int) {
//        symptomsInput.add("Batuk" to answer)
        symptomsInput.add(symptom to answer)
    }


    private val _diseasePrediction =
        MutableStateFlow<Result<DiseasePredictionResponse>>(Result.Loading)
    val diseasePrediction = _diseasePrediction

    fun getDiseasePrediction() {
        viewModelScope.launch {
            val symptomsMap = symptomsInput.toMap()
            predictionRepository.getDiseasePrediction(symptomsMap).collect {result ->
                _diseasePrediction.value = result
            }
        }
    }

    private val _symptomsData = MutableStateFlow<Result<SymptomsResponse>>(Result.Loading)
    val symptomsData get() = _symptomsData

    fun fetchSymptoms() {
        viewModelScope.launch {
            predictionRepository.getSymptomsCat().collect {
                _symptomsData.value = it
            }
        }
    }

    fun resetQuestionStatus() {
        isQuestionAnswered = false
        symptomsInput.clear()
    }

}