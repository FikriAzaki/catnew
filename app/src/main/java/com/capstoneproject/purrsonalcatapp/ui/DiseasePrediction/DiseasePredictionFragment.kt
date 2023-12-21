package com.capstoneproject.purrsonalcatapp.ui.DiseasePrediction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstoneproject.purrsonalcatapp.R
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.local.pref.dataStore
import com.capstoneproject.purrsonalcatapp.data.remote.response.DiseasePredictionResponse
import com.capstoneproject.purrsonalcatapp.data.remote.response.SymptomsResponse
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.repository.PredictionRepository
import com.capstoneproject.purrsonalcatapp.databinding.FragmentDiseasePredictionBinding
import com.capstoneproject.purrsonalcatapp.ui.MainViewModelFactory
import com.capstoneproject.purrsonalcatapp.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.widget.*
import androidx.appcompat.app.AlertDialog


class DiseasePredictionFragment : Fragment() {

    private lateinit var viewModel: DiseasePredictionViewModel
    private lateinit var authPreferences: AuthPreferences
    private lateinit var binding: FragmentDiseasePredictionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiseasePredictionBinding.inflate(inflater, container, false)
        authPreferences = AuthPreferences(requireContext().dataStore)
        return binding.root
    }

    private var currentQuestionIndex = 0
    private val symptomsList = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val token = authPreferences.authToken.first()
            val apiService = ApiConfig.getApiService(token)
            val repository = PredictionRepository(apiService, authPreferences)

            viewModel = ViewModelProvider(
                this@DiseasePredictionFragment,
                MainViewModelFactory(repository)
            ).get(DiseasePredictionViewModel::class.java)

            Log.d("DiseasePredictionFragment", "ViewModel and repository initialized")
        }

        binding.btnYes.setOnClickListener {
            onButtonClicked(1)
        }
        binding.btnNo.setOnClickListener {
            onButtonClicked(0)
        }
        binding.btnReset.setOnClickListener {
            resetFragment()
        }

        lifecycleScope.launch {
            viewModel.fetchSymptoms()
        }

        observeSymptoms()
        observeDiseasePrediction()
    }

    private fun resetFragment() {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragment = DiseasePredictionFragment()

        fragmentTransaction.replace(R.id.fragment_container, fragment)

        fragmentTransaction.commit()
    }


    private fun observeSymptoms() {
        lifecycleScope.launch {
            viewModel.symptomsData.collect { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data
                        updateSymptomsList(data)
                        updateQuestion()
                    }

                    is Result.Error -> {
                        val errorMessage = result.error
                        Log.e("DiseasePredictionFragment", errorMessage)
                    }

                    Result.Loading -> {
                    }
                }
            }
        }
    }

    private fun updateSymptomsList(symptomsResponse: SymptomsResponse) {
        symptomsList.clear()
        symptomsList.addAll((symptomsResponse.data ?: emptyList()) as Collection<String>)
    }


    private fun observeDiseasePrediction() {
        lifecycleScope.launch {
            viewModel.diseasePrediction.collect { result ->
                Log.d("ResultObserveDiseasePrediction", "Result: $result")
                when (result) {
                    is Result.Success -> {
                        val data = result.data.data?.predictions
//                        binding.tvPredictionResult.text = "Disease: $data"
                        showPredictionDialog(data)

                    }

                    is Result.Error -> {
                        val errorMessage = result.error
                        Log.e("ERROR OBSERVE DISEASE", "ERROR OBSERVE DISEASE: $errorMessage")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showPredictionDialog(predictions: String?) {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_layout, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()
        dialogView.findViewById<TextView>(R.id.textMessage). text ="Kucing anda terkena penyakit: $predictions"

        dialogView.findViewById<Button>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
            resetFragment()
        }

        dialog.show()
    }


    private fun onAllInputCollected() {
        Log.d("SymptomsForRequest", "Symptoms for Request: $symptomsList")
        viewModel.getDiseasePrediction()
        Log.d("SuccessCallDiseasePredictionFragment", "Calling getDiseasePrediction()")
    }

    private fun onButtonClicked(answer: Int) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < symptomsList.size) {
            val symptom = symptomsList[currentQuestionIndex]
            viewModel.addSymptom(symptom, answer)
            Log.d("AnswerSymptom", "Symptom: $symptom, Answer: $answer")

            // Pembaruan tampilan hanya dilakukan jika tidak melebihi ukuran gejala
            if (currentQuestionIndex < symptomsList.size - 1) {
                // Pindahkan ke gejala selanjutnya
                currentQuestionIndex++
                updateQuestion()
            } else {
                Toast.makeText(requireContext(), "Menunggu hasil prediksi...", Toast.LENGTH_SHORT)
                    .show()

                // Jika sudah mencapai akhir gejala, lakukan sesuatu (misalnya, panggil fungsi onAllInputCollected())
                onAllInputCollected()
                Log.d("PREDICT", "SUCCESS")
            }
        } else {
            // Handle the case where the index is out of bounds
            onAllInputCollected()
            Log.d("PREDICT", "SUCCESS")
        }
    }

    private fun updateQuestion() {
        if (currentQuestionIndex < symptomsList.size) {
            binding.tvGejala.text = symptomsList[currentQuestionIndex]
        }
    }
}