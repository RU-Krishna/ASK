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
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.feature.auth.R
import com.feature.auth.stateHandler.HandleAuthenticationState
import com.feature.auth.viewModels.SignUpViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit = {},
    viewModel: SignUpViewModel
) { // Function to navigate to Sign In screen

    val (email, password, confirmPassword) = arrayOf(
        viewModel.email,
        viewModel.password,
        viewModel.confirmPassword
    )

    val authState = viewModel.authenticationState.collectAsState().value

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }


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
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            // App Logo and Name
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "ASk",
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
                    viewModel.changeEmail(it)
                    viewModel.validateEmail()

                },
                label = { Text("Email") },
                modifier = modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = !viewModel.isEmailValid,
                supportingText = {
                    Text(
                        text = "Please enter a valid email address.",
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Left,
                        modifier = modifier
                            .fillMaxWidth(),
                        lineHeight = 2.em

                    )
                }
            )

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.changePassword(it)
                    viewModel.validatePasswords()
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
                    imeAction = ImeAction.Next
                ),
                isError = !viewModel.isPasswordValid,
                supportingText = {
                    Text(
                        text = "Password must have:\n" +
                                "• 1 Small Letter\n" +
                                "• 1 Capital Letter\n" +
                                "• 1 Digit\n" +
                                "• 1 Special Symbol &\n" +
                                "• Length: 8 - 16 characters.",
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Left,
                        modifier = modifier
                            .fillMaxWidth(),
                        lineHeight = 2.em

                    )
                }
            )

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    viewModel.changeConfirmPassword(it)
                    viewModel.validatePasswords()
                },
                label = { Text("Confirm Password") },
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        confirmPasswordVisibility = !confirmPasswordVisibility
                    }) {
                        Icon(
                            painter = painterResource(
                                id =
                                if (confirmPasswordVisibility) R.drawable.visibility else R.drawable.visibility_off
                            ),
                            contentDescription = "Confirm Password Visibility"
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirm Password") },
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = !viewModel.isConfirmPasswordValid
            )

            // Sign Up Button
            FilledTonalButton(
                onClick = {
                    viewModel.createAccount()
                },
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = viewModel.isEmailValid && viewModel.isPasswordValid && viewModel.isConfirmPasswordValid
            ) {
                Text("Sign Up")
            }

            // Navigate to Sign In
            TextButton(onClick = navigateToSignIn) {
                Text("Already have an account? Sign In")
            }
        }
        HandleAuthenticationState(
            modifier = modifier.align(Alignment.Center),
            authState = authState,
            successMessage = "Account Created 👍",
            navigateCallback = {
                navigateToSignIn()
            }
        )
    }

}