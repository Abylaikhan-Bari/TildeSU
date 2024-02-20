package com.ashim_bari.tildesu.viewmodel.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository

class ExerciseViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
