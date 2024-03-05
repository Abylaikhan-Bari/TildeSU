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
    init {
        // Add logging for initial values
        Log.d(TAG, "Initial values: quizScore=${_quizScore.value}, trueFalseScore=${_trueFalseScore.value}, puzzleScore=${_puzzleScore.value}, exerciseCompleted=${_exerciseCompleted.value}, quizPassed=${_quizPassed.value}")
    }

    // Setter functions with logging
    private fun setQuizScore(value: Int) {
        _quizScore.value = value
        Log.d(TAG, "Quiz Score Updated to: $value")
    }

    private fun setTrueFalseScore(value: Int) {
        _trueFalseScore.value = value
        Log.d(TAG, "True False Score Updated to: $value")
    }

    private fun setPuzzleScore(value: Int) {
        _puzzleScore.value = value
        Log.d(TAG, "Puzzle Score Updated to: $value")
    }

    private fun setExerciseCompleted(value: Boolean) {
        _exerciseCompleted.value = value
        Log.d(TAG, "Exercise Completed: $value")
    }

    private fun setQuizPassed(value: Boolean?) {
        _quizPassed.value = value
        Log.d(TAG, "Quiz Passed: $value")
    }

    fun loadExercisesForLevelAndType(level: String, type: ExerciseType) {
        currentLevelId = level
        currentExerciseType = type
        viewModelScope.launch {
            try {
                val exercisesList = repository.getExercisesByLevelAndType(level, type)
                if (exercisesList.isNotEmpty()) {
                    _exercises.value = exercisesList
                    _currentExerciseIndex.value = 0
                    resetExercise()
                    Log.d(TAG, "Exercises loaded for level: $level, type: $type, total: ${exercisesList.size}")
                } else {
                    Log.d(TAG, "No exercises found for level: $level, type: $type")
                }
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
                    val newScore = (_quizScore.value ?: 0) + 1
                    setQuizScore(newScore)
                }
                moveToNextQuiz()
            } else {
                Log.d(TAG, "Non-Quiz exercise attempted in Quiz method. Exercise type: ${exercise.type}")
            }
        }
    }

    fun moveToNextQuiz() {
        _currentExerciseIndex.value?.let { currentIndex ->
            if (currentIndex + 1 < (_exercises.value?.size ?: 0)) {
                _currentExerciseIndex.value = currentIndex + 1
                Log.d(TAG, "Moved to next question: index ${_currentExerciseIndex.value}")
            } else {
                setQuizPassed(true)
                completeExercise()
            }
        }
    }

    fun submitTrueFalseAnswer(userAnswer: Boolean) {
        val currentExercise = _exercises.value?.get(_currentExerciseIndex.value ?: 0)
        Log.d(TAG, "Attempting True/False answer. Current type: ${currentExercise?.type}, Index: ${_currentExerciseIndex.value}")

        if (currentExercise?.type != ExerciseType.TRUE_FALSE) {
            Log.d(TAG, "Non-True/False exercise attempted in True/False method. Exercise type: ${currentExercise?.type}")
            return
        }
        val isCorrect = userAnswer == currentExercise.isTrue
        if (isCorrect) {
            val newScore = (_trueFalseScore.value ?: 0) + 1
            setTrueFalseScore(newScore)
            updateProgress()
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
            setPuzzleScore(newScore)
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

    private fun updateProgress() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            currentLevelId?.let { levelId ->
                viewModelScope.launch {
                    try {
                        // Fetch the current progress for the level
                        val currentProgress = repository.fetchUserProgress(userId, levelId)

                        // Prepare the updated data based on the exercise type
                        val updatedData = when (currentExerciseType) {
                            ExerciseType.QUIZ -> mapOf(
                                "quizCorrect" to (_quizScore.value ?: 0) + (currentProgress["quizCorrect"] as? Number ?: 0).toInt(),
                                "quizTotal" to (_exercises.value?.size ?: 0)
                            )
                            ExerciseType.TRUE_FALSE -> mapOf(
                                "trueFalseCorrect" to (_trueFalseScore.value ?: 0) + (currentProgress["trueFalseCorrect"] as? Number ?: 0).toInt(),
                                "trueFalseTotal" to (_exercises.value?.size ?: 0)
                            )
                            ExerciseType.PUZZLES -> mapOf(
                                "puzzleCorrect" to (_puzzleScore.value ?: 0) + (currentProgress["puzzleCorrect"] as? Number ?: 0).toInt(),
                                "puzzleTotal" to (_exercises.value?.size ?: 0)
                            )
                            else -> emptyMap()
                        }


                        // Ensure there's something to update
                        if (updatedData.isNotEmpty()) {
                            // Include common fields that should always be updated
                            // Inside your updateProgress method
                            val commonData = mapOf(
                                "overallTotal" to calculateOverallTotalQuestions(currentProgress, updatedData),
                                "overallCorrect" to calculateOverallCorrectAnswers(currentProgress, updatedData),
                                "completedOn" to FieldValue.serverTimestamp()
                            )


                            repository.updateUserProgress(userId, levelId, updatedData + commonData)
                            Log.d(TAG, "Progress updated for user: $userId, level: $levelId with data: $updatedData")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update progress for user: $userId, level: $levelId", e)
                    }
                }
            }
        }
    }

    private fun calculateOverallTotalQuestions(currentProgress: Map<String, Any>, updatedData: Map<String, Any>): Int {
        // Ensure values are Int and handle nullability
        val quizTotal = (updatedData["quizTotal"] as? Number ?: currentProgress["quizTotal"] as? Number ?: 0).toInt()
        val trueFalseTotal = (updatedData["trueFalseTotal"] as? Number ?: currentProgress["trueFalseTotal"] as? Number ?: 0).toInt()
        val puzzleTotal = (updatedData["puzzleTotal"] as? Number ?: currentProgress["puzzleTotal"] as? Number ?: 0).toInt()

        return quizTotal + trueFalseTotal + puzzleTotal
    }

    private fun calculateOverallCorrectAnswers(currentProgress: Map<String, Any>, updatedData: Map<String, Any>): Int {
        // Ensure values are Int and handle nullability
        val quizCorrect = (updatedData["quizCorrect"] as? Number ?: currentProgress["quizCorrect"] as? Number ?: 0).toInt()
        val trueFalseCorrect = (updatedData["trueFalseCorrect"] as? Number ?: currentProgress["trueFalseCorrect"] as? Number ?: 0).toInt()
        val puzzleCorrect = (updatedData["puzzleCorrect"] as? Number ?: currentProgress["puzzleCorrect"] as? Number ?: 0).toInt()

        return quizCorrect + trueFalseCorrect + puzzleCorrect
    }





    fun completeExercise() {
        setExerciseCompleted(true)

        updateProgress()
    }

    fun resetExercise() {
        _currentExerciseIndex.value = 0
        setExerciseCompleted(false)
        setQuizScore(0)
        setTrueFalseScore(0)
        setPuzzleScore(0)
        setQuizPassed(null)
    }
}