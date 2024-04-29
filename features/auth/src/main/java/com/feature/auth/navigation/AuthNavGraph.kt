package com.feature.auth.navigation


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.feature.auth.ui.ForgotPasswordScreen
import com.feature.auth.ui.SignInScreen
import com.feature.auth.ui.SignUpScreen
import com.feature.auth.viewModels.ForgotPasswordViewModel
import com.feature.auth.viewModels.SignInViewModel
import com.feature.auth.viewModels.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth

// ... (AuthScreen enum class remains the same) ...
enum class AuthScreen(val route: String) {
    SignIn(  route = "SignIn"),
    SignUp(route = "SignUp"),
    ForgotPassword(route = "ForgotPassword")
}


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    firebaseAuth: FirebaseAuth,
) {
    navigation(
        startDestination = AuthScreen.SignIn.route,
        route = "auth"
    ) {
        // Sign In Screen
        composable(
            route = AuthScreen.SignIn.route,
            enterTransition = { // Enter transition for SignIn screen
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            }
        ) {
            val viewModel = SignInViewModel(firebaseAuth)
            SignInScreen(
                viewModel = viewModel,
                navigateToSignUp = {
                    navController.navigate(AuthScreen.SignUp.route)
                                     },
                navigateToForgotPassword = {
                    navController.navigate(AuthScreen.ForgotPassword.route)
                }
            )
        }

        // Sign Up Screen
        composable(
            route = AuthScreen.SignUp.route,
            enterTransition = { // Enter transition for SignUp screen
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            }
        ) {
            val viewModel = SignUpViewModel(firebaseAuth)
            SignUpScreen(
                viewModel = viewModel,
                navigateToSignIn = {
                    navController.popBackStack(route = AuthScreen.SignIn.route, inclusive = false)
                }
            )
        }

        // Forgot Password Screen
        composable(
            route = AuthScreen.ForgotPassword.route,
            enterTransition = { // Enter transition for ForgotPassword screen
                slideInVertically(
                    initialOffsetY = { 1000 },
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            }
        ) {
            val viewModel = ForgotPasswordViewModel(firebaseAuth)
            ForgotPasswordScreen(
                viewModel = viewModel,
                navigateToSignIn = { navController.popBackStack(route = AuthScreen.SignIn.route, inclusive = false) }
            )
        }
    }
}