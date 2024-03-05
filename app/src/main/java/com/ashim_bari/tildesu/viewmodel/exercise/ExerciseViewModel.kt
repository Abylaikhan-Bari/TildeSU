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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

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



    // Inside submitQuizAnswer method
    fun submitQuizAnswer(selectedOption: Int) {
        val currentExercise = _exercises.value?.get(_currentExerciseIndex.value ?: 0)
        currentExercise?.let { exercise ->
            if (exercise.type == ExerciseType.QUIZ) {
                val isCorrect = selectedOption == exercise.correctOptionIndex
                if (isCorrect) {
                    val newScore = (_quizScore.value ?: 0) + 1
                    _quizScore.value = newScore
                    Log.d("ExerciseVM", "Quiz Score Updated to: $newScore")
                }
                moveToNextQuiz()
            } else {
                Log.d("ExerciseVM", "Non-Quiz exercise attempted in Quiz method. Exercise type: ${exercise.type}")
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
    fun submitTrueFalseAnswer(userAnswer: Boolean, currentExercise: Exercise) {
        if (currentExercise.type == ExerciseType.TRUE_FALSE) {
            val isCorrect = userAnswer == currentExercise.isTrue
            Log.d("ExerciseVM", "True/False Answer Submitted: User answer is $userAnswer, Correct answer is ${currentExercise.isTrue}, Correct: $isCorrect")

            if (isCorrect) {
                val newScore = (_trueFalseScore.value ?: 0) + 1
                _trueFalseScore.postValue(newScore)
                Log.d("ExerciseVM", "True/False Score Updated to: $newScore")
            }
            moveToNextTrueFalse()
        } else {
            Log.d("ExerciseVM", "Non-True/False exercise attempted in True/False method.")
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
            _puzzleScore.value = newScore
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
                        // Calculate individual exercise type counts
                        val totalQuizQuestions = _exercises.value?.count { it.type == ExerciseType.QUIZ } ?: 0
                        val totalTrueFalseQuestions = _exercises.value?.count { it.type == ExerciseType.TRUE_FALSE } ?: 0
                        val totalPuzzleQuestions = _exercises.value?.count { it.type == ExerciseType.PUZZLES } ?: 0

                        // Calculate overall correct answers
                        val quizCorrectAnswers = _quizScore.value ?: 0
                        val trueFalseCorrectAnswers = _trueFalseScore.value ?: 0
                        val puzzleCorrectAnswers = _puzzleScore.value ?: 0
                        val overallCorrectAnswers = quizCorrectAnswers + trueFalseCorrectAnswers + puzzleCorrectAnswers

                        // Calculate overall questions using set to remove duplicates
                        val overallTotalQuestions = _exercises.value?.toSet()?.size ?: 0

                        val scoresMap = mapOf(
                            "quiz" to mapOf(
                                "correctAnswers" to quizCorrectAnswers,
                                "totalQuestions" to totalQuizQuestions
                            ),
                            "true_false" to mapOf(
                                "correctAnswers" to trueFalseCorrectAnswers,
                                "totalQuestions" to totalTrueFalseQuestions
                            ),
                            "puzzles" to mapOf(
                                "correctAnswers" to puzzleCorrectAnswers,
                                "totalQuestions" to totalPuzzleQuestions
                            )
                        )

                        val overallScoresMap = mapOf(
                            "correctAnswers" to overallCorrectAnswers,
                            "totalQuestions" to overallTotalQuestions
                        )

                        val updateData = mapOf(
                            "scores" to scoresMap,
                            "overallScore" to overallScoresMap,
                            "completedOn" to FieldValue.serverTimestamp()
                        )

                        // Check for cancellation using coroutineContext
                        if (coroutineContext[Job]!!.isCancelled) {
                            Log.w(TAG, "Job was cancelled for user: $userId, level: $levelId")
                            // **Fix cancellation logic here (replace with your solution)**
                        } else {
                            repository.updateUserProgress(userId, levelId, updateData)
                        }

                        // Logging
                        Log.d(TAG, "Progress updated for user: $userId, level: $levelId")
                        // ... rest of your logging code
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            Log.w(TAG, "Job was cancelled for user: $userId, level: $levelId", e)
                        } else {
                            Log.e(TAG, "Failed to update progress for user: $userId, level: $levelId", e)
                        }
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