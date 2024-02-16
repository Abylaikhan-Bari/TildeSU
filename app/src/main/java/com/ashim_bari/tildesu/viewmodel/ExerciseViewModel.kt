package com.ashim_bari.tildesu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    // Function to set the current level and load exercises for that level
    fun setCurrentLevel(level: String) {
        loadExercises(level)
    }

    private fun loadExercises(level: String) {
        viewModelScope.launch {
            _exercises.value = repository.getExercisesByLevel(level)
        }
    }

    // Add methods to handle user actions and logic for quizzes, such as answering questions
}

