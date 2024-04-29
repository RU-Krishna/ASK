package com.example.gemini.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gemini.MyApplication
import com.example.gemini.viewModel.RecordViewModel
import com.features.ask.viewmodel.AskViewModel

val geminiViewModel = object: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication

        val repository = application.chatContainer.firebaseRepo

        return AskViewModel(
            firebaseRepo = repository
        ) as T
    }
}

val recordViewModel = object: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as MyApplication


        val auth = application.firebaseAuth

        val recordViewModel = RecordViewModel()

        recordViewModel.initRecordRepo(application.recordContainer.chatRecordRepo)
        return recordViewModel as T
    }
}