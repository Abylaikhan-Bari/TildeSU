package com.ashim_bari.tildesu.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>?>()
    val exercises: MutableLiveData<List<Exercise>?> = _exercises

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    init {
        loadExercisesForLevel("A1")
    }

    fun loadExercisesForLevel(level: String) {
        viewModelScope.launch {
            _exercises.value = repository.getExercisesByLevel(level)
        }
    }

    fun submitAnswer(selectedOption: Int) {
        val currentQuestion = _exercises.value?.get(_currentQuestionIndex.value ?: 0) ?: return
        if (selectedOption == currentQuestion.correctOption) {
            _score.value = _score.value?.plus(1)
        }
        // Update the current question with the user's selected option for UI feedback
        val updatedExercises = _exercises.value?.toMutableList()
        updatedExercises?.set(_currentQuestionIndex.value ?: 0, currentQuestion.copy(userSelectedOption = selectedOption))
        _exercises.value = updatedExercises
    }

    fun moveToNextQuestion() {
        val nextIndex = (_currentQuestionIndex.value ?: 0) + 1
        if (nextIndex < (_exercises.value?.size ?: 0)) {
            _currentQuestionIndex.value = nextIndex
        } else {
            // Handle quiz completion
        }
    }
}




