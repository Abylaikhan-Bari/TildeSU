package com.ashim_bari.tildesu.viewmodel.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.google.firebase.auth.FirebaseAuth
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

    private var currentLevelId: String? = null

    fun submitAnswer(selectedOption: Int) {
        val currentQuestion = exercises.value?.getOrNull(currentQuestionIndex.value ?: 0)
        Log.d("ExerciseVM", "Selected Option: $selectedOption, Correct Option: ${currentQuestion?.correctOptionIndex}")
        if (currentQuestion != null) {
            if (selectedOption == currentQuestion.correctOptionIndex) {
                Log.d("ExerciseVM", "Answer is correct")
                _score.value = (_score.value ?: 0) + 1
                _totalCorrectAnswers.value = (_totalCorrectAnswers.value ?: 0) + 1
            } else {
                Log.d("ExerciseVM", "Answer is incorrect")
            }
            moveToNextQuestion()
        }
    }

    fun loadExercisesForLevel(level: String) {
        Log.d("ExerciseVM", "Loading exercises for level: $level")
        currentLevelId = level
        viewModelScope.launch {
            val exercisesList = repository.getExercisesByLevel(level)
            if (exercisesList.isNotEmpty()) {
                _exercises.value = exercisesList
                _currentQuestionIndex.value = 0
                _score.value = 0
                _quizCompleted.value = false
                _totalCorrectAnswers.value = 0
                Log.d("ExerciseVM", "Exercises loaded for level $level: ${exercisesList.size} exercises")
            } else {
                Log.e("ExerciseVM", "No exercises found for level $level")
            }
        }
    }

    fun moveToNextQuestion() {
        val totalQuestions = exercises.value?.size ?: 0
        val nextIndex = (_currentQuestionIndex.value ?: 0) + 1

        Log.d("ExerciseVM", "Current index: ${_currentQuestionIndex.value}, Next index: $nextIndex, Total questions: $totalQuestions")

        if (nextIndex < totalQuestions) {
            _currentQuestionIndex.value = nextIndex
            Log.d("ExerciseVM", "Moving to next question: $nextIndex")
        } else {
            completeQuiz()
        }
    }

    private fun completeQuiz() {
        Log.d("ExerciseVM", "Completing quiz")
        _quizCompleted.value = true
        // Existing logic to update user progress
    }
}





