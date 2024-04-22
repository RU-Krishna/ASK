package com.example.gemini.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.gemini.viewModel.RecordViewModel
import com.example.gemini.viewModelFactory.geminiViewModel
import com.example.gemini.viewModelFactory.recordViewModel
import com.features.gemini.ui.GeminiHomeScreen
import com.features.gemini.viewmodel.GeminiViewModel
import com.google.firebase.auth.FirebaseUser

fun NavGraphBuilder.appGraph(
    currentUser: FirebaseUser?,
    logOut: () -> Unit
) {


    navigation(
        startDestination = MainApp.HomeScreen.screen,
        route = "home"
    ) {

        composable(
            route = MainApp.HomeScreen.screen,
            enterTransition = {
                this.slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                this.slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left
                )
            }
        ) {

            //View Model Creation from View Model Provider Factories.
            val geminiViewModel: GeminiViewModel = viewModel(factory = geminiViewModel)
            val recordViewModel: RecordViewModel = viewModel(factory = recordViewModel)

            //Assigning reference to the databases where, data is stored and retrieved...
            recordViewModel.recordRepo.createReference("${currentUser?.uid}/Record")
            geminiViewModel.firebaseRepo.createReference("${currentUser?.uid}/Chats")

            //Store all the records.
            val chatRecord by recordViewModel.chatRecords.collectAsStateWithLifecycle()

            //Listen for the records in the firebase at the creation of the app.
            recordViewModel.showRecords()

            //Current RecordId
            val currentRecordId by recordViewModel.currentChatId


            //Hoist the state of the chat Record
            val chat by geminiViewModel.chats.collectAsState()

            GeminiHomeScreen(
                viewModel = geminiViewModel,
                isLandingPage = recordViewModel.isLandingPage.value,
                modifyLandingPage = recordViewModel::modifyLandingPage,
                records = chatRecord,
                onRecordClick = {
                    //Changing the current record id, so that operations are done on that id.
                    recordViewModel.changeCurrentChat(it)
                    //Listening for the records at the provided id.
                    geminiViewModel.listenChanges(it)
                },
                currentChatId = currentRecordId,
                changeCurrentChat = recordViewModel::changeCurrentChat,
                chat = chat,
                onDeleteRecord = { recordId ->
                    recordViewModel.deleteRecord(recordId)
                    geminiViewModel.deleteChat(recordId, "")
                    recordViewModel.noRecordOrEmptyChat()
                },
                logOut = logOut
            )

        }
    }

}

enum class MainApp(val screen: String) {
    HomeScreen(screen = "HomeScreen")
}