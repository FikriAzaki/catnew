package com.capstoneproject.purrsonalcatapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class DiseasePredictionResponse(

    @field:SerializedName("data")
    val data: DataPrediction? = null,

    @field:SerializedName("error")
    val error: Boolean? = null
)

data class DataPrediction(

    @field:SerializedName("predictions")
    val predictions: String? = null
)


data class Answer(val answer: Map<String, Int>)

