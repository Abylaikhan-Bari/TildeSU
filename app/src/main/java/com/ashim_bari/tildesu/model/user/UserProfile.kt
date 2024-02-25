package com.ashim_bari.tildesu.model.user

data class UserProfile(
    val email: String = "", // Default empty string for non-nullable String
    val name: String? = null,
    val surname: String? = null,
    val city: String? = null,
    val age: String? = null, // Assuming age is stored as a String; adjust accordingly
    val gender: Int? = 0, // Use null for gender not set
    val specialty: String? = null
)
