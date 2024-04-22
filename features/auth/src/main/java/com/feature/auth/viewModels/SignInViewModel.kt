package com.feature.auth.viewModels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feature.auth.authState.AuthenticationState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    // Private mutable state flow for authentication state
    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    // Public immutable state flow exposed to the UI
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState.asStateFlow()

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun signIn() {
        viewModelScope.launch {
            // Check if email and password are not empty
            if (email.isBlank() || password.isBlank()) {
                _authenticationState.value = AuthenticationState.Failure("Email and password cannot be empty")
                return@launch
            }
            _authenticationState.value = AuthenticationState.Authenticating
            signInWithEmailAndPassword(email, password) // Call private sign-in function
        }
    }

    private suspend fun signInWithEmailAndPassword(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        _authenticationState.value = AuthenticationState.Success
                    }
                    .addOnFailureListener { exception ->
                        _authenticationState.value = AuthenticationState.Failure(exception.message ?: "Sign In failed")
                    }
            } catch (e: Exception) {
                _authenticationState.value = AuthenticationState.Failure(e.message ?: "An error occurred")
            }
        }
    }
}