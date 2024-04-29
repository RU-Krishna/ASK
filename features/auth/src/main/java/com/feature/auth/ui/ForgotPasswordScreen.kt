package com.feature.auth.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feature.auth.stateHandler.HandleForgotPasswordState
import com.feature.auth.viewModels.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit = {},
    viewModel: ForgotPasswordViewModel
) {

    val email = viewModel.email
    val forgotPasswordState = viewModel.forgotPasswordLinkState.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
    )
    {
        Column(
            modifier = modifier
                .matchParentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Top)
        ) {
            // App Logo
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Ask",
                    fontFamily = FontFamily.Serif,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                )
            }

            Text("Enter your email to get the reset password link.")


            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )


            // Send Button
            Button(
                onClick = {
                    viewModel.sendPasswordResetEmail()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Send")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
        HandleForgotPasswordState(
            forgotPasswordState = forgotPasswordState,
            navigateToSignInScreen = navigateToSignIn
        )


    }



}