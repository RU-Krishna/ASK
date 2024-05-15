package com.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feature.auth.R
import com.feature.auth.stateHandler.HandleAuthenticationState
import com.feature.auth.viewModels.SignInViewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToSignUp: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    viewModel: SignInViewModel
) {
    val email = viewModel.email
    val password = viewModel.password
    val authState = viewModel.authenticationState.collectAsState().value
    var passwordVisibility by remember { mutableStateOf(false) }



    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .matchParentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically)
        ) {
            // App Logo and Name
           Column(
               verticalArrangement = Arrangement.Center,
               modifier = modifier
                   .padding(vertical = 16.dp)
           ) {
               Text(
                   text = "ASK",
                   fontFamily = FontFamily.Serif,
                   fontSize = 48.sp,
                   fontWeight = FontWeight.Bold,
                   fontStyle = FontStyle.Italic,
               )
           }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    viewModel.updateEmail(it)
                },
                label = { Text("Email") },
                modifier = modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )


            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.updatePassword(it)
                },
                label = { Text("Password") },
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = painterResource(
                                id =
                                if (passwordVisibility) R.drawable.visibility else R.drawable.visibility_off
                            ),
                            contentDescription = "Password Visibility"
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Forgot Password
            TextButton(
                onClick = navigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }


            // Sign In Button
            Button(
                onClick = {
                    viewModel.signIn()
                },
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = email.trim().isNotEmpty() && password.trim().isNotEmpty()
            ) {
                Text("Sign In")
            }

            // Navigate to Sign Up
            TextButton(onClick = navigateToSignUp) {
                Text("New User? Create Account")
            }
        }

        HandleAuthenticationState(
            modifier = modifier.align(Alignment.Center),
            authState = authState,
            successMessage = "Sign In 👍",
            navigateCallback = {

            }
        )
    }
}