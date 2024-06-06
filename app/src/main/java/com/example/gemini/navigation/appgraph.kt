package com.example.gemini.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.common.ui.settings.SettingsScreen
import com.data.model.settings.ModelSettings
import com.example.gemini.viewModel.RecordViewModel
import com.example.gemini.viewModelFactory.geminiViewModel
import com.example.gemini.viewModelFactory.recordViewModel
import com.features.ask.ui.GeminiHomeScreen
import com.features.ask.viewmodel.AskViewModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar


//Main Screen Navigation Graph Builder...
fun NavGraphBuilder.appGraph(
    navController: NavHostController,
    currentUser: FirebaseUser?,
    logOut: () -> Unit
) {

    var askViewModel: AskViewModel? = null

    //Graph Builder navigation function for defining navigation of the main app.
    navigation(
        startDestination = MainApp.HomeScreen.screen,
        route = "home"
    ) {

        //Main Screen Composable...
        composable(
            route = MainApp.HomeScreen.screen,
            enterTransition = {
                this.slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                this.slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                )
            }
        ) {

            //View Model Creation from View Model Provider Factories.
            val geminiViewModel: AskViewModel = viewModel(factory = geminiViewModel)
            val recordViewModel: RecordViewModel = viewModel(factory = recordViewModel)

            askViewModel = geminiViewModel

            //Assigning reference to the databases where, data is stored and retrieved...
            recordViewModel.recordRepo.createReference("${currentUser?.uid}/Record")
            geminiViewModel.firebaseRepo.createReference("${currentUser?.uid}/Chats")

            //Store all the records.
            val chatRecord by recordViewModel.chatRecords.collectAsStateWithLifecycle()

            //Listen for the records in the firebase at the creation of the app.
            recordViewModel.showRecords()

            //Current RecordId
            val currentRecordId by recordViewModel.currentRecordId


            //Hoist the state of the chat Record
            val chat by geminiViewModel.chats.collectAsState()

            GeminiHomeScreen(
                viewModel = geminiViewModel,
                isLandingPage = recordViewModel.isLandingPage.value,
                modifyLandingPage = recordViewModel::modifyLandingPage,
                records = chatRecord,
                onRecordClick = {
                    //Changing the current record id, so that operations are done on that id.
                    recordViewModel.changeCurrentRecord(it)
                    //Listening for the records at the provided id.
                    geminiViewModel.listenChanges(it)
                },
                currentChatId = currentRecordId,
                changeCurrentChat = recordViewModel::changeCurrentRecord,
                chat = chat,
                onDeleteRecord = { recordId ->
                    recordViewModel.deleteRecord(recordId)
                    geminiViewModel.deleteChat(recordId, "")
                    if(currentRecordId == recordId)
                        recordViewModel.noRecordOrEmptyChat()
                },
                logOut = logOut,
                addNewChat = {
                    val newRecordId = Calendar.getInstance().time.toString()
                    recordViewModel.addRecord(newRecordId)
                    recordViewModel.changeCurrentRecord(newRecordId)
                    geminiViewModel.listenChanges(newRecordId)
                },
                answerThis = {
                    val newRecordId = Calendar.getInstance().time.toString()
                    recordViewModel.addRecord(newRecordId)
                    recordViewModel.changeCurrentRecord(newRecordId)
                    geminiViewModel.raiseQuery(newRecordId, it)
                },
                renameTitle = recordViewModel::changeRecordName,
                userName = currentUser?.email?.replaceAfter("@", "")?:"User",
                askSettings = {
                    navController.navigate(MainApp.SettingsScreen.screen) {
                        this.restoreState = true
                    }
                }
            )

        }
        composable(
            route = MainApp.SettingsScreen.screen,
            enterTransition = {
                this.slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                this.slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                )
            }
        ) {

            val modelSetting = askViewModel?.modelSettingsFlow?.collectAsState()?.value ?: ModelSettings()

            var model by remember {
                mutableStateOf(modelSetting)
            }

                SettingsScreen(
                    onBackClick = {
                        navController
                            .popBackStack(route = MainApp.HomeScreen.screen, inclusive = false)
                    },
                    updateTemp = {
                        model = model.copy(temperature = it)
                    },
                    updateTopK = {
                        model = model.copy(topK = it)
                    },
                    updateTopP = {
                        model = model.copy(topP = it)
                    },
                    changeHarassment = {
                        model =  model.copy(harassment = returnBlockThreshold(it))
                    },
                    changeHateSpeech = {
                        model = model.copy(hateSpeech = returnBlockThreshold(it))
                    },
                    changeSexualContent = {
                        model = model.copy(sexualContent = returnBlockThreshold(it))
                    },
                    changeDangerousContent = {
                        model = model.copy(dangerous = returnBlockThreshold(it))
                    },
                    modelSettings = model,
                    onDone = {
                        askViewModel?.updateModelSettings(
                            model
                        )
                    }
                )



        }
    }

}


fun returnBlockThreshold(value: Float): String {
    return when(value) {
        0f -> BlockThreshold.NONE.name
        1f -> BlockThreshold.LOW_AND_ABOVE.name
        2f -> BlockThreshold.MEDIUM_AND_ABOVE.name
        3f -> BlockThreshold.ONLY_HIGH.name
        else -> BlockThreshold.UNSPECIFIED.name
    }
}







enum class MainApp(val screen: String) {
    HomeScreen(screen = "HomeScreen"),
    SettingsScreen(screen = "SettingsScreen")
}