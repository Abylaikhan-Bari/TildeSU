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
    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    private val _quizCompleted = MutableLiveData<Boolean>(false)
    val quizCompleted: LiveData<Boolean> = _quizCompleted

    private var currentLevelId: String? = null


    init {
        // Optionally, load initial data here or trigger from the UI
    }

    fun loadExercisesForLevel(level: String) {
        viewModelScope.launch {
            try {
                val exercisesList = repository.getExercisesByLevel(level)
                if (exercisesList.isNotEmpty()) {
                    _exercises.value = exercisesList
                    _currentQuestionIndex.value = 0
                    _score.value = 0
                    _quizCompleted.value = false
                    currentLevelId = level
                    Log.d("ExerciseVM", "Exercises loaded, total: ${exercisesList.size}, Level: $level")
                } else {
                    Log.w("ExerciseVM", "No exercises found for level $level")
                }
            } catch (e: Exception) {
                Log.e("ExerciseVM", "Error loading exercises for level $level", e)
            }
        }
    }

    fun submitAnswer(selectedOption: Int) {
        exercises.value?.let {
            if (it.isNotEmpty()) {
                val currentExercise = it[_currentQuestionIndex.value ?: 0]
                val isCorrect = selectedOption == currentExercise.correctOptionIndex
                if (isCorrect) {
                    _score.value = (_score.value ?: 0) + 1
                    Log.d("ExerciseVM", "Correct answer, score updated: ${_score.value}")
                }
                moveToNextQuestion()
            }
        }
    }

    fun moveToNextQuestion() {
        val currentIdx = currentQuestionIndex.value ?: 0
        val nextIndex = currentIdx + 1
        val totalQuestions = exercises.value?.size ?: 0

        Log.d("ExerciseVM", "Current index: $currentIdx, Next index: $nextIndex, Total questions: $totalQuestions")

        if (nextIndex < totalQuestions) {
            _currentQuestionIndex.postValue(nextIndex)
            Log.d("ExerciseVM", "Moving to next question: Index $nextIndex")
        } else {
            Log.d("ExerciseVM", "No more questions. Marking quiz as completed.")
            completeQuiz()
        }
    }


//    private fun completeQuiz() {
//        Log.d("ExerciseVM", "Completing quiz")
//        _quizCompleted.value = true
//        // Update user progress and other cleanup as necessary
//    }

    private fun completeQuiz() {
        Log.d("ExerciseVM", "Completing quiz")
        _quizCompleted.value = true
        // Call updateProgress here to trigger the update in Firestore
        updateProgress()
    }

    private fun updateProgress() {
        // Obtain the current user's ID and email from FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: run {
            Log.e("ExerciseVM", "User not logged in.")
            return
        }
        val email = currentUser.email ?: run {
            Log.e("ExerciseVM", "User email not available.")
            return
        }

        val score = _score.value ?: 0
        val totalCorrectAnswers = score // Assuming score represents the total correct answers

        currentLevelId?.let { levelId ->
            viewModelScope.launch {
                try {
                    repository.updateUserProgress(userId, levelId, score, email, totalCorrectAnswers)
                    Log.d("ExerciseVM", "User progress updated for level $levelId")
                } catch (e: Exception) {
                    Log.e("ExerciseVM", "Error updating user progress for level $levelId", e)
                }
            }
        }
    }


}





