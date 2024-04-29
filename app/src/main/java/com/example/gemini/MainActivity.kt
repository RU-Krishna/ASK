package com.example.gemini

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.common.ui.screens.AskSplashScreen
import com.example.gemini.navigation.appGraph
import com.example.gemini.ui.theme.GeminiTheme
import com.example.gemini.viewModel.AuthViewModel
import com.example.gemini.viewModel.ConnectivityManagerViewModel
import com.feature.auth.navigation.authNavGraph
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {

    //Variable creation for accessing it in other callbacks.
    private lateinit var authXViewModel: AuthViewModel

    //ConnectivityManager class variable, which hears for network State Changes.
    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var networkViewModel: ConnectivityManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialising the network State variable at the creation of the app.
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkViewModel = ConnectivityManagerViewModel(connectivityManager)


        setContent {
            GeminiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var isSplashScreen by rememberSaveable {
                        mutableStateOf(true)
                    }

                    var route by rememberSaveable {
                        mutableStateOf("splash")
                    }

                    val networkState = networkViewModel.networkState

                    val authViewModel: AuthViewModel by  viewModels(factoryProducer = { AuthViewModel.factory })

                    val currentUser = authViewModel.currentUserState.collectAsState().value

                    val navController = rememberNavController()


                    LaunchedEffect(key1 = currentUser, key2 = isSplashScreen) {
                        if(isSplashScreen){
                            navController.navigate("splash"){
                                popUpTo("splash")
                            }
                        }
                        else if (currentUser == null) {
                            route = "auth"
                            navController.navigate("auth") {
                                popUpTo("auth")
                            }

                        }
                        else {
                            route = "home"
                            navController.navigate("home") {
                                popUpTo("home")
                            }
                            delay(2000)
                            Toast
                                .makeText(
                                    this@MainActivity,
                                    "Welcome \n${
                                        currentUser.email?.replace(
                                            "@gmail.com",
                                            "."
                                        )
                                    }",
                                    LENGTH_LONG
                                )
                                .show()
                        }
                    }


                    NavHost(
                        navController = navController,
                        startDestination = route,
                    ) {

                        composable(
                            route = "splash"
                        ) {
                            AskSplashScreen {
                                authXViewModel = authViewModel

                                authViewModel.addAuthStateListener()

                                isSplashScreen = false
                            }
                        }


                        authNavGraph(
                            navController = navController,
                            firebaseAuth = authViewModel.provideAuthObject()
                        )

                        appGraph(
                            currentUser = currentUser,
                            logOut = {
                                Toast
                                    .makeText(
                                        this@MainActivity,
                                        "👋🏻👋🏻${currentUser?.email?.replace("@gmail.com", ".")}",
                                        LENGTH_LONG
                                    )
                                    .show()
                                authViewModel.logOut()
                            },
                        )

                    }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        //Registering the authStateListener in order to check whether user validates or not...
        authXViewModel.addAuthStateListener()
    }


    override fun onStart() {
        super.onStart()
        networkViewModel.registerNetworkCallback()
    }


    override fun onPause() {
        super.onPause()
        networkViewModel.unRegisterNetworkCallback()
    }

    override fun onStop() {
        super.onStop()
        //Removing authenticate State Listener in "onStop" aka when activity is in background...
        authXViewModel.removeAuthStateListener()
    }

}





