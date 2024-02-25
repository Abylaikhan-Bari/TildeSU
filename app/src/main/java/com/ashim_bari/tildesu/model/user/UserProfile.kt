package com.ashim_bari.tildesu.model.user

data class UserProfile(
    val email: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val city: String? = null,
    val age: String? = null,
    val gender: Int? = null,
    val specialty: String? = null
)
