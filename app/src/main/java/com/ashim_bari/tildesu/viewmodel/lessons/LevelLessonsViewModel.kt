package com.ashim_bari.tildesu.viewmodel.lessons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.lesson.Lesson
import com.ashim_bari.tildesu.model.lesson.LessonsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelLessonsViewModel @Inject constructor(
    private val lessonsRepository: LessonsRepository
) : ViewModel() {

    private val _lesson = MutableLiveData<Lesson?>()
    val lesson: MutableLiveData<Lesson?> = _lesson

    fun fetchLesson(level: String, lessonId: String) {
        viewModelScope.launch {
            lessonsRepository.getLessonById(level, lessonId)
                .catch { e ->
                    // Handle the error, maybe log it or show an error message
                    _lesson.postValue(null)  // Posting null or a default error state
                }
                .collect { lesson ->
                    _lesson.postValue(lesson)
                }
        }
    }
}
