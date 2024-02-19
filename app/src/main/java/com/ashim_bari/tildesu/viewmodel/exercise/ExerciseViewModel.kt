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
        currentLevelId = level // Store the current level ID
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

    private fun completeQuiz() {
        viewModelScope.launch {
            _quizCompleted.value = true // Mark the quiz as completed
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userId = firebaseUser?.uid
            val email = firebaseUser?.email
            val score = _score.value ?: 0
            val totalCorrectAnswers = _totalCorrectAnswers.value ?: 0 // Retrieve total correct answers
            val levelId = currentLevelId  // Use the stored level ID
            if (userId != null && levelId != null && email != null) {
                repository.updateUserProgress(userId, levelId, score, email, totalCorrectAnswers) // Pass totalCorrectAnswers here
            }
        }
    }


    fun moveToNextQuestion() {
        val nextIndex = (_currentQuestionIndex.value ?: 0) + 1
        if (nextIndex < (_exercises.value?.size ?: 0)) {
            _currentQuestionIndex.value = nextIndex
        } else {
            completeQuiz() // Call completeQuiz when there are no more questions
        }
    }

}





