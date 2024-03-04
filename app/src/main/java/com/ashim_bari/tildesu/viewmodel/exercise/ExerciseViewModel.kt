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
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    // Separate score for each exercise type
    private val _quizScore = MutableLiveData(0)
    val quizScore: LiveData<Int> = _quizScore
    private val _trueFalseScore = MutableLiveData(0)
    val trueFalseScore: LiveData<Int> = _trueFalseScore
    private val _puzzleScore = MutableLiveData(0)
    val puzzleScore: LiveData<Int> = _puzzleScore

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
                resetScores()
                _exerciseCompleted.value = false
                Log.d(TAG, "Exercises loaded for level: $level, type: $type, total: ${exercisesList.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading exercises for level $level and type $type", e)
            }
        }
    }

    private fun resetScores() {
        _quizScore.value = 0
        _trueFalseScore.value = 0
        _puzzleScore.value = 0
    }

    fun submitQuizAnswer(selectedOption: Int) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.QUIZ) {
                val isCorrect = selectedOption == exercise.correctOptionIndex
                if (isCorrect) {
                    _quizScore.value = (_quizScore.value ?: 0) + 1
                }
                moveToNextQuestion()
            }
        }
    }

    fun submitTrueFalseAnswer(userAnswer: Boolean) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.TRUE_FALSE) {
                val isCorrect = userAnswer == exercise.isTrue
                if (isCorrect) {
                    _trueFalseScore.value = (_trueFalseScore.value ?: 0) + 1
                }
                moveToNextQuestion()
            }
        }
    }

    fun submitPuzzleAnswer(userOrder: List<Int>, puzzle: Exercise) {
        val currentExercise = _exercises.value?.get(_currentQuestionIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.PUZZLES) {
                val isCorrect = userOrder == exercise.correctOrder
                if (isCorrect) {
                    _puzzleScore.value = (_puzzleScore.value ?: 0) + 1
                    moveToNextPuzzle()
                }
            }
        }
    }


    fun updateProgress() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            currentLevelId?.let { levelId ->
                viewModelScope.launch {
                    try {
                        // Create maps for each exercise type
                        val quizMap = mapOf(
                            "correctAnswers" to (_quizScore.value ?: 0),
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.QUIZ } ?: 0)
                        )
                        val puzzlesMap = mapOf(
                            "correctAnswers" to (_puzzleScore.value ?: 0),
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.PUZZLES } ?: 0)
                        )
                        val trueFalseMap = mapOf(
                            "correctAnswers" to (_trueFalseScore.value ?: 0),
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.TRUE_FALSE } ?: 0)
                        )

                        // Calculate the overall score
                        val overallCorrectAnswers = _quizScore.value!! + _puzzleScore.value!! + _trueFalseScore.value!!
                        val overallTotalQuestions = _exercises.value?.size ?: 0

                        // Create a map for scores and overall score
                        val scoresMap = mapOf(
                            "quiz" to quizMap,
                            "puzzles" to puzzlesMap,
                            "true_false" to trueFalseMap
                        )
                        val overallScoresMap = mapOf(
                            "correctAnswers" to overallCorrectAnswers,
                            "totalQuestions" to overallTotalQuestions
                        )

                        // Combine everything into the update data
                        val updateData = mapOf(
                            "scores" to scoresMap,
                            "overallScore" to overallScoresMap,
                            "completedOn" to FieldValue.serverTimestamp()
                        )

                        repository.updateUserProgress(userId, levelId, updateData)
                        Log.d(TAG, "Progress updated for user: $userId, level: $levelId")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update progress for user: $userId, level: $levelId", e)
                    }
                }
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
        _quizPassed.value = true// Example threshold for passing
        updateProgress()
    }



    fun resetExercise() {
        _currentQuestionIndex.value = 0
        _exerciseCompleted.value = false
        _quizPassed.value = null
    }
}