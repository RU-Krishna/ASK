package com.example.gemini.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gemini.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: FirebaseAuth
): ViewModel() {

    private var currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState = currentUser.asStateFlow()

    private val listener =   FirebaseAuth.AuthStateListener {
        viewModelScope.launch {
            currentUser.emit(it.currentUser)
        }
    }

    fun addAuthStateListener() {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener() {
        auth.removeAuthStateListener(listener)
    }

    fun logOut() {
        auth.signOut()
    }

    fun provideAuthObject(): FirebaseAuth {
        return  auth
    }



    companion object {
        val factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val application = (extras[APPLICATION_KEY] as MyApplication)

                val auth = application.firebaseAuth

                return AuthViewModel(
                    auth = auth
                ) as T
            }

        }
    }


}