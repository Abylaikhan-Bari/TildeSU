package com.ashim_bari.tildesu.model.exercise

data class Exercise(
    var id: String = "",
    val level: String = "",
    val question: String = "",
    val options: List<String> = listOf(),
    val correctOption: Int = -1,
    var userSelectedOption: Int = -1 // New property to track user's selection
)
