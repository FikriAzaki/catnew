package com.capstoneproject.purrsonalcatapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("data")
	val data: DataRegister? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataRegister(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
