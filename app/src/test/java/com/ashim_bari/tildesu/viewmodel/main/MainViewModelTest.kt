package com.ashim_bari.tildesu.viewmodel.main

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.model.user.UserProfile
import com.ashim_bari.tildesu.model.user.UserRepository
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var navController: NavHostController

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        mainViewModel = MainViewModel(userRepository)
    }

    @Test
    fun testFetchUserProfile() = runTest(testDispatcher) {
        val userProfile = UserProfile(email = "test@example.com")
        doAnswer { invocation ->
            val callback = invocation.arguments[0] as (UserProfile?) -> Unit
            callback(userProfile)
            null
        }.`when`(userRepository).getUserProfile(any())

        val observer = Observer<UserProfile?> {}
        try {
            mainViewModel.userProfile.observeForever(observer)
            mainViewModel.fetchUserProfile()

            val result = mainViewModel.userProfile.value
            assertEquals(userProfile, result)
        } finally {
            mainViewModel.userProfile.removeObserver(observer)
        }
    }

    @Test
    fun testLogout() = runTest(testDispatcher) {
        `when`(userRepository.logout()).thenReturn(true)

        mainViewModel.logout(navController)

        verify(navController).navigate(Navigation.AUTHENTICATION_ROUTE)
    }

    @Test
    fun testLoadUserProgress() = runTest(testDispatcher) {
        val userProgress = mapOf("A1" to UserRepository.UserProgress(0.8f, 0.5f, 0.6f, 0.7f, 0.4f))
        `when`(userRepository.getUserProgress(anyString())).thenReturn(userProgress)

        val observer = Observer<Map<String, UserRepository.UserProgress>> {}
        try {
            mainViewModel.progressData.observeForever(observer)
            mainViewModel.loadUserProgress()

            val result = mainViewModel.progressData.value
            assertEquals(userProgress, result)
        } finally {
            mainViewModel.progressData.removeObserver(observer)
        }
    }

    @Test
    fun testUploadProfileImage() = runTest(testDispatcher) {
        val uri = mock(Uri::class.java)
        val imageUrl = "http://example.com/image.jpg"
        `when`(userRepository.uploadUserImage(uri)).thenReturn(imageUrl)

        val observer = Observer<String?> {}
        try {
            mainViewModel.profileImageUrl.observeForever(observer)
            mainViewModel.uploadProfileImage(uri)

            val result = mainViewModel.profileImageUrl.value
            assertEquals(imageUrl, result)
        } finally {
            mainViewModel.profileImageUrl.removeObserver(observer)
        }
    }

    @Test
    fun testUpdatePassword() = runTest(testDispatcher) {
        val newPassword = "newPassword"
        val currentPassword = "currentPassword"
        `when`(userRepository.updatePassword(newPassword, currentPassword)).thenReturn(true)

        mainViewModel.updatePassword(newPassword, currentPassword) { success ->
            assertEquals(true, success)
        }

        verify(userRepository).updatePassword(newPassword, currentPassword)
    }
}
