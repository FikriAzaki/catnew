package com.capstoneproject.purrsonalcatapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.repository.PredictionRepository
import com.capstoneproject.purrsonalcatapp.ui.DiseasePrediction.DiseasePredictionViewModel

class MainViewModelFactory(private val predictionRepository: PredictionRepository, authPreferences: AuthPreferences? = null): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiseasePredictionViewModel::class.java)) {
            return DiseasePredictionViewModel(predictionRepository) as T
        }

        throw IllegalArgumentException("Auth preference is required")
    }
}