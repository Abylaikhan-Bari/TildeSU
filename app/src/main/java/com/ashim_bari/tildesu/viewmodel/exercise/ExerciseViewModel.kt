package com.ashim_bari.tildesu.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>?>(emptyList())
    val exercises: LiveData<List<Exercise>?> = _exercises

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizCompleted = MutableLiveData<Boolean>(false)
    val quizCompleted: LiveData<Boolean> = _quizCompleted

    private val _totalCorrectAnswers = MutableLiveData<Int>(0)
    val totalCorrectAnswers: LiveData<Int> = _totalCorrectAnswers

    fun loadExercisesForLevel(level: String) {
        viewModelScope.launch {
            // You might want to handle any potential exceptions here
            val exercisesList = repository.getExercisesByLevel(level)
            _exercises.value = exercisesList
            _currentQuestionIndex.value = 0 // Reset the index whenever loading new exercises
            _score.value = 0 // Reset the score
            _quizCompleted.value = false // Reset quiz completion status
            _totalCorrectAnswers.value = 0 // Reset total correct answers
        }
    }

    fun submitAnswer(selectedOption: Int) {
        val currentQuestion = exercises.value?.getOrNull(currentQuestionIndex.value ?: 0) ?: return
        if (selectedOption == currentQuestion.correctOption) {
            _score.value = (_score.value ?: 0) + 1
            _totalCorrectAnswers.value = (_totalCorrectAnswers.value ?: 0) + 1
        }
        moveToNextQuestion()
    }

    fun moveToNextQuestion() {
        val nextIndex = (_currentQuestionIndex.value ?: 0) + 1
        if (nextIndex < (exercises.value?.size ?: 0)) {
            _currentQuestionIndex.value = nextIndex
        } else {
            _quizCompleted.value = true // Quiz is completed
        }
    }
}





