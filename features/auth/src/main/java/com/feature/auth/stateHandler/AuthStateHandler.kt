package com.feature.auth.stateHandler


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.feature.auth.authState.AuthenticationState
import kotlinx.coroutines.delay

@Composable
fun HandleAuthenticationState(
    modifier: Modifier = Modifier,
    authState: AuthenticationState,
    successMessage: String,
    navigateCallback:() -> Unit
) {
    val context = LocalContext.current

    when (authState) {
        AuthenticationState.Authenticating -> {
            // Show progress indicator while authenticating
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthenticationState.Failure -> {
            // Display error message in a Toast
            LaunchedEffect(key1 = authState) {
                Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            }
        }
        AuthenticationState.Success -> {
            // Display success message in a Toast
            LaunchedEffect(key1 = authState) {
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                delay(2000)
                navigateCallback()
            }
        }

        else -> {

        }
    }
}