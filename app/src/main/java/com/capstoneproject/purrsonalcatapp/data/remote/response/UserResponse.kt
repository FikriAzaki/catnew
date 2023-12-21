package com.capstoneproject.purrsonalcatapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("data")
    val data: DataUser? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class DataUser(


    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("iat")
    val iat: Int? = null,

    @field:SerializedName("exp")
    val exp: Int? = null,

    )
