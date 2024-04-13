package com.ashim_bari.tildesu.viewmodel.lessons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.lesson.Lesson
import com.ashim_bari.tildesu.model.lesson.LessonsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val lessonsRepository: LessonsRepository
) : ViewModel() {

    // LiveData to hold the list of lessons
    private val _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> = _lessons

    // Function to call the repository and fetch lessons
    fun fetchLessonsForLevel(level: String) {
        viewModelScope.launch {
            lessonsRepository.getLessonsForLevel(level).collect { lessonList ->
                _lessons.value = lessonList
            }
        }
    }
}
