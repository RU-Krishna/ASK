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


class SignUpViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    var email by mutableStateOf("")
    private set
    var password by mutableStateOf("")
    private set
    var confirmPassword by mutableStateOf("")
    private set

    var isEmailValid by mutableStateOf(false)
    private set

    var isPasswordValid by mutableStateOf(false)
    private set

    var isConfirmPasswordValid by mutableStateOf(false)
    private set

    // Private mutable state flow for authentication state
    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    // Publicly exposed state flow (read-only)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState.asStateFlow()


    //Functions for changing the email and password from the ViewModel.
    fun changeEmail(email:String) {
        this.email = email
    }

    fun changePassword(password:String) {
        this.password = password
    }

    fun changeConfirmPassword(confirmPassword: String) {
        this.confirmPassword = confirmPassword
    }


    fun validateEmail() {
        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
    }

    fun validatePasswords() {
        isPasswordValid = isPasswordStrong(password) && password.isNotEmpty()
        isConfirmPasswordValid = password == confirmPassword && confirmPassword.isNotEmpty()
    }

    // Password strength check using regex
    private fun isPasswordStrong(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,16}\$")
        return passwordRegex.matches(password)
    }

    fun createAccount() {
        signUp(email, password)
    }


    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authenticationState.value = AuthenticationState.Authenticating
            withContext(Dispatchers.IO) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authenticationState.value = AuthenticationState.Success
                            // TODO: Navigate to the main screen or perform other actions on successful sign-up
                        } else {
                            val errorMessage = task.exception?.message ?: "Sign up failed"
                            _authenticationState.value = AuthenticationState.Failure(errorMessage)
                        }
                    }
            }
        }
    }
}