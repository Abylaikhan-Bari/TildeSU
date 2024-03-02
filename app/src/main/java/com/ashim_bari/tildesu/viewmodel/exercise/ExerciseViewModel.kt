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

    private val _exerciseCompleted = MutableLiveData<Boolean>(false)
    val exerciseCompleted: LiveData<Boolean> = _exerciseCompleted

    private var currentLevelId: String? = null
    private var currentExerciseType: ExerciseType? = null

    private val _quizPassed = MutableLiveData<Boolean?>(null)
    val quizPassed: LiveData<Boolean?> = _quizPassed
    private val TAG = "ExerciseViewModel"
    fun loadExercisesForLevelAndType(level: String, type: ExerciseType) {
        currentLevelId = level
        currentExerciseType = type
        viewModelScope.launch {
            try {
                val exercisesList = repository.getExercisesByLevelAndType(level, type)
                _exercises.value = exercisesList
                _currentQuestionIndex.value = 0
                _score.value = 0
                _exerciseCompleted.value = false
                Log.d("ExerciseVM", "Exercises loaded for level: $level, type: $type, total: ${exercisesList.size}")
            } catch (e: Exception) {
                Log.e("ExerciseVM", "Error loading exercises for level $level and type $type", e)
            }
        }
    }

    fun submitQuizAnswer(selectedOption: Int) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.QUIZ) {
                val isCorrect = selectedOption == exercise.correctOptionIndex
                if (isCorrect) {
                    _score.value = (_score.value ?: 0) + 1
                }
                // Move to the next question regardless of the answer being correct or not
                moveToNextQuestion()
            }
        }
    }


    fun submitTrueFalseAnswer(userAnswer: Boolean) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.TRUE_FALSE) {
                // Check if isTrue is not null. If it is, log an error or handle it accordingly.
                if (exercise.isTrue == null) {
                    Log.e("ExerciseVM", "Error: isTrue is null for exercise ${exercise.id}")
                    // Consider handling this error appropriately, e.g., showing an error message to the user.
                } else {
                    val isCorrect = userAnswer == exercise.isTrue
                    Log.d("ExerciseVM", "User answer: $userAnswer, Actual answer: ${exercise.isTrue}, Evaluated Correct: $isCorrect")

                    if (isCorrect) {
                        _score.value = (_score.value ?: 0) + 1
                    }
                    moveToNextQuestion()
                }
            }
        }
    }


    fun submitPuzzleAnswer(userOrder: List<Int>, puzzle: Exercise) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.PUZZLES) {
                val isCorrect = userOrder == exercise.correctOrder
                if (isCorrect) {
                    _score.value = (_score.value ?: 0) + 1
                    Log.d(TAG, "Puzzle answer is correct. Score incremented: ${_score.value}")
                    Log.d("ExerciseVM", "Puzzle solved correctly.")
                    moveToNextPuzzle()
                }
                else{
                    Log.d("ExerciseVM", "Puzzle attempt incorrect.")
                }
                moveToNextQuestion()
                // Optionally handle incorrect answers or give feedback
            }
        }
    }

    fun moveToNextPuzzle() {
        val currentIndex = _currentQuestionIndex.value ?: return
        if (currentIndex + 1 < (_exercises.value?.size ?: 0)) {
            _currentQuestionIndex.value = currentIndex + 1
            Log.d(TAG, "Moved to next puzzle: index ${_currentQuestionIndex.value}")
        } else {
            _exerciseCompleted.value = true
        }
    }




    fun moveToNextQuestion() {
    _currentQuestionIndex.value?.let { currentIndex ->
        if (currentIndex + 1 < (_exercises.value?.size ?: 0)) {
            _currentQuestionIndex.value = currentIndex + 1
            Log.d("ExerciseVM", "Moved to next question: index ${_currentQuestionIndex.value}")
        } else {
            completeExercise()
        }
    }
}


    fun completeExercise() {
        _exerciseCompleted.value = true
        val totalQuestions = _exercises.value?.size ?: 0
        val totalCorrectAnswers = _score.value ?: 0
        _quizPassed.value = totalCorrectAnswers.toFloat() / totalQuestions >= 0.5 // Example threshold for passing
        updateProgress()
    }

    fun updateProgress() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            currentLevelId?.let { levelId ->
                viewModelScope.launch {
                    try {
                        val totalQuestions = _exercises.value?.size ?: 0
                        val correctAnswers = _score.value ?: 0
                        repository.updateUserProgress(userId, levelId, currentExerciseType!!, correctAnswers, totalQuestions)
                        Log.d("ExerciseVM", "Progress updated for user: $userId, level: $levelId")
                    } catch (e: Exception) {
                        Log.e("ExerciseVM", "Failed to update progress for user: $userId, level: $levelId", e)
                    }
                }
            }
        }
    }

    fun resetExercise() {
        _currentQuestionIndex.value = 0
        _score.value = 0
        _exerciseCompleted.value = false
        _quizPassed.value = null
    }
}