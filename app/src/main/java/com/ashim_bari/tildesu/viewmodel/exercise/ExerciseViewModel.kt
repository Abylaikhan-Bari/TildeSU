package com.ashim_bari.tildesu.viewmodel.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseRepository
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    private val _quizCompleted = MutableLiveData<Boolean>(false)
    val quizCompleted: LiveData<Boolean> = _quizCompleted

    private var currentLevelId: String? = null
    private var currentExerciseType: ExerciseType? = null

    private val _quizPassed = MutableLiveData<Boolean?>(null)
    val quizPassed: LiveData<Boolean?> = _quizPassed

    fun loadExercisesForLevelAndType(level: String, type: ExerciseType) {
        currentLevelId = level
        currentExerciseType = type
        viewModelScope.launch {
            try {
                // Ensure 'level' corresponds to document IDs in Firestore (e.g., "A1", "A2", etc.)
                val exercisesList = repository.getExercisesByLevelAndType(level, type)
                if (exercisesList.isNotEmpty()) {
                    _exercises.value = exercisesList
                    _currentQuestionIndex.value = 0
                    _score.value = 0
                    _quizCompleted.value = false
                    Log.d("ExerciseVM", "Exercises loaded for level: $level, type: $type, total: ${exercisesList.size}")
                } else {
                    Log.w("ExerciseVM", "No exercises found for level $level and type $type")
                }
            } catch (e: Exception) {
                Log.e("ExerciseVM", "Error loading exercises for level $level and type $type", e)
            }
        }
    }


    fun submitAnswer(selectedOption: Int) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let {
            // Assuming you want to check if selectedOption matches the correctOptionIndex
            val isCorrect = selectedOption == it.correctOptionIndex
            if (isCorrect) {
                _score.value = (_score.value ?: 0) + 1
            }
            moveToNextQuestion()
        }
    }



    fun moveToNextQuestion() {
        _currentQuestionIndex.value?.let { currentIndex ->
            if (currentIndex + 1 < _exercises.value?.size ?: 0) {
                _currentQuestionIndex.value = currentIndex + 1
            } else {
                completeQuiz()
            }
        }
    }

    private fun completeQuiz() {
        _quizCompleted.value = true
        val totalQuestions = _exercises.value?.size ?: 0
        val totalCorrectAnswers = _score.value ?: 0
        _quizPassed.value = totalCorrectAnswers >= totalQuestions // Adjust this logic as per your requirements
        updateProgress()
    }

    private fun updateProgress() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            currentLevelId?.let { levelId ->
                viewModelScope.launch {
                    try {
                        val totalQuestions = _exercises.value?.size ?: 0
                        val score = _score.value ?: 0
                        repository.updateUserProgress(userId, levelId, score, score, totalQuestions) // Assuming score is equal to total correct answers
                        Log.d("ExerciseVM", "Progress updated for user: $userId, level: $levelId")
                    } catch (e: Exception) {
                        Log.e("ExerciseVM", "Failed to update progress for user: $userId, level: $levelId", e)
                    }
                }
            }
        }
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _score.value = 0
        _quizCompleted.value = false
        _quizPassed.value = null
    }
}




