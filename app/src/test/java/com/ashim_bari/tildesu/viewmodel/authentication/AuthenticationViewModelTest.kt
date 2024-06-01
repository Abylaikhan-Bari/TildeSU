package com.ashim_bari.tildesu.viewmodel.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ashim_bari.tildesu.model.user.UserRepository
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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version to emulate
class AuthenticationViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var authenticationViewModel: AuthenticationViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        authenticationViewModel = AuthenticationViewModel(userRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLogin() = runTest(testDispatcher) {
        val email = "testuser@example.com"
        val password = "testpass"
        Mockito.`when`(userRepository.loginUser(email, password)).thenReturn(true)

        val result = authenticationViewModel.login(email, password)

        assertEquals(true, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testIsEmailRegistered() = runTest(testDispatcher) {
        val email = "testuser@example.com"
        Mockito.`when`(userRepository.isEmailRegistered(email)).thenReturn(true)

        val result = authenticationViewModel.isEmailRegistered(email)

        assertEquals(true, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRegister() = runTest(testDispatcher) {
        val email = "testuser@example.com"
        val password = "testpass"
        Mockito.`when`(userRepository.registerUser(email, password)).thenReturn(true)

        val result = authenticationViewModel.register(email, password)

        assertEquals(true, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testResetPassword() = runTest(testDispatcher) {
        val email = "testuser@example.com"
        Mockito.`when`(userRepository.resetPassword(email)).thenReturn(true)

        val result = authenticationViewModel.resetPassword(email)

        assertEquals(true, result)
    }
}
