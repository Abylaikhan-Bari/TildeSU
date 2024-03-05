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

    private val _currentExerciseIndex = MutableLiveData<Int>(0)
    val currentExercisesIndex: LiveData<Int> = _currentExerciseIndex

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
                _currentExerciseIndex.value = 0
                resetExercise()
                _exerciseCompleted.value = false
                Log.d(TAG, "Exercises loaded for level: $level, type: $type, total: ${exercisesList.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading exercises for level $level and type $type", e)
            }
        }
    }



    fun submitQuizAnswer(selectedOption: Int) {
        val currentExercise = _exercises.value?.get(_currentExerciseIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.QUIZ) {
                val isCorrect = selectedOption == exercise.correctOptionIndex
                if (isCorrect) {
                    _quizScore.value = (_quizScore.value ?: 0) + 1
                }
                moveToNextQuiz()
            }
        }
    }
    fun moveToNextQuiz() {
        _currentExerciseIndex.value?.let { currentIndex ->
            if (currentIndex + 1 < (_exercises.value?.size ?: 0)) {
                _currentExerciseIndex.value = currentIndex + 1
                Log.d("ExerciseVM", "Moved to next question: index ${_currentExerciseIndex.value}")
            } else {
                completeExercise()
            }
        }
    }
    fun submitTrueFalseAnswer(userAnswer: Boolean) {
        val currentExercise = _exercises.value?.get(_currentExerciseIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.TRUE_FALSE) {
                val isCorrect = userAnswer == exercise.isTrue
                if (isCorrect) {
                    _trueFalseScore.value = (_trueFalseScore.value ?: 0) + 1
                }
                moveToNextTrueFalse()
            }
        }
    }
    fun moveToNextTrueFalse() {
        val nextIndex = (_currentExerciseIndex.value ?: 0) + 1
        if (nextIndex < (_exercises.value?.size ?: 0)) {
            _currentExerciseIndex.value = nextIndex
        } else {
            completeExercise()
        }
    }

    fun submitPuzzleAnswer(userOrder: List<Int>, puzzle: Exercise) {
        val isCorrect = userOrder == puzzle.correctOrder
        if (isCorrect) {
            val newScore = (_puzzleScore.value ?: 0) + 1
            Log.d(TAG, "Updating score from ${_puzzleScore.value} to $newScore")
            _puzzleScore.value = newScore // Or use postValue if updating from a background thread
            Log.d(TAG, "Score updated to ${_puzzleScore.value}")
        }
    }


    fun moveToNextPuzzle() {
        val currentIndex = _currentExerciseIndex.value ?: return
        if (currentIndex + 1 < (_exercises.value?.size ?: 0)) {
            _currentExerciseIndex.value = currentIndex + 1

        } else {
            completeExercise()
        }
    }



    fun updateProgress() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            currentLevelId?.let { levelId ->
                viewModelScope.launch {
                    try {
                        // Calculate the individual and overall scores
                        val overallCorrectAnswers = _quizScore.value!! + _puzzleScore.value!! + _trueFalseScore.value!!
                        val overallTotalQuestions = _exercises.value?.size ?: 0

                        // Create a map for each exercise type scores
                        val quizScores = mapOf(
                            "correctAnswers" to _quizScore.value!!,
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.QUIZ } ?: 0)
                        )
                        val puzzleScores = mapOf(
                            "correctAnswers" to _puzzleScore.value!!,
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.PUZZLES } ?: 0)
                        )
                        val trueFalseScores = mapOf(
                            "correctAnswers" to _trueFalseScore.value!!,
                            "totalQuestions" to (_exercises.value?.count { it.type == ExerciseType.TRUE_FALSE } ?: 0)
                        )

                        // Create a combined map for overall scores
                        val overallScores = mapOf(
                            "correctAnswers" to overallCorrectAnswers,
                            "totalQuestions" to overallTotalQuestions
                        )

                        // Combine all scores into the update data map
                        val updateData = mapOf(
                            "scores" to mapOf(
                                "quiz" to quizScores,
                                "puzzles" to puzzleScores,
                                "true_false" to trueFalseScores
                            ),
                            "overallScore" to overallScores,
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











    fun completeExercise() {
        _exerciseCompleted.value = true
        _quizPassed.value = true// Example threshold for passing
        updateProgress()
    }



    fun resetExercise() {
        _currentExerciseIndex.value = 0
        _exerciseCompleted.value = false
        _quizScore.value = 0
        _trueFalseScore.value = 0
        _puzzleScore.value = 0
        _quizPassed.value = null
    }

}