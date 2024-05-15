package com.feature.auth.stateHandler


import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.feature.auth.viewModels.ForgotPasswordLinkState
import kotlinx.coroutines.delay

@Composable
fun HandleForgotPasswordState(
    forgotPasswordState: ForgotPasswordLinkState,
    navigateToSignInScreen: () -> Unit
) {
    val context = LocalContext.current

    when (forgotPasswordState) {
        is ForgotPasswordLinkState.Success -> {
            LaunchedEffect(key1 = Unit) {
                Toast.makeText(context, forgotPasswordState.message, Toast.LENGTH_SHORT).show()
                delay(1000)
                navigateToSignInScreen()
            }
        }
        is ForgotPasswordLinkState.Failure -> {
            LaunchedEffect(key1 = Unit) {
                Toast.makeText(context, forgotPasswordState.message, Toast.LENGTH_SHORT).show()
            }

        }
        else -> {

        }


    }
}