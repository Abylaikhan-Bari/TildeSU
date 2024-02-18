package com.ashim_bari.tildesu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise

import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>> = _exercises

    fun loadExercisesForLevel(level: String) {
        viewModelScope.launch {
            _exercises.value = repository.getExercisesByLevel(level)
        }
    }
}



