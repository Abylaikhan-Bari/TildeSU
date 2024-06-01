package com.ashim_bari.tildesu.viewmodel.lessons

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ashim_bari.tildesu.model.lesson.Lesson
import com.ashim_bari.tildesu.model.lesson.LessonsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LessonsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var lessonsRepository: LessonsRepository

    private lateinit var lessonsViewModel: LessonsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        lessonsViewModel = LessonsViewModel(lessonsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchLessonsForLevel() = runBlockingTest {
        val lessons = listOf(
            Lesson(id = "1", title = "Lesson 1", description = "", content = ""),
            Lesson(id = "2", title = "Lesson 2", description = "", content = "")
        )
        `when`(lessonsRepository.getLessonsForLevel("A1")).thenReturn(flowOf(lessons))

        val observer = Observer<List<Lesson>> {}
        try {
            lessonsViewModel.lessons.observeForever(observer)
            lessonsViewModel.fetchLessonsForLevel("A1")

            val result = lessonsViewModel.lessons.value
            assertEquals(lessons, result)
        } finally {
            lessonsViewModel.lessons.removeObserver(observer)
        }
    }

}
