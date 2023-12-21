package com.capstoneproject.purrsonalcatapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class SymptomsResponse(

	@field:SerializedName("data")
	val data: List<String?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)
