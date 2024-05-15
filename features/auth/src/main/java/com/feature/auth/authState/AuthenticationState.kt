package com.feature.auth.authState


sealed interface AuthenticationState {
    data object Idle: AuthenticationState
    data object Success : AuthenticationState
    data class Failure(val message: String) : AuthenticationState
    data object Authenticating : AuthenticationState
}