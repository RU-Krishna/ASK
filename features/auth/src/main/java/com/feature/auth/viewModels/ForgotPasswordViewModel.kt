package com.feature.auth.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    private val _forgotPasswordLinkState = MutableStateFlow<ForgotPasswordLinkState>(ForgotPasswordLinkState.Idle)
    val forgotPasswordLinkState: StateFlow<ForgotPasswordLinkState> = _forgotPasswordLinkState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun sendPasswordResetEmail() {
        if (email.isBlank()) {
            _forgotPasswordLinkState.value = ForgotPasswordLinkState.Failure("Email cannot be empty")
            return
        }

        viewModelScope.launch {
            _forgotPasswordLinkState.value = ForgotPasswordLinkState.Loading
            sendPasswordResetEmailInternal(email)
        }
    }

    private suspend fun sendPasswordResetEmailInternal(email: String) {
        withContext(Dispatchers.IO) {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    _forgotPasswordLinkState.value = ForgotPasswordLinkState.Success("Reset link sent to your email")
                }
                .addOnFailureListener { exception ->
                    _forgotPasswordLinkState.value = ForgotPasswordLinkState.Failure(exception.message ?: "Failed to send reset link")
                }
        }
    }
}

sealed interface ForgotPasswordLinkState {
    data object Idle : ForgotPasswordLinkState
    data object Loading : ForgotPasswordLinkState
    data class Success(val message: String) : ForgotPasswordLinkState
    data class Failure(val message: String) : ForgotPasswordLinkState
}