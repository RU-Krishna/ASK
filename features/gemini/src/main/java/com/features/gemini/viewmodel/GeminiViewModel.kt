package com.features.gemini.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.firebaseData.Chat
import com.data.model.repo.FirebaseRepo
import com.features.gemini.BuildConfig
import com.features.gemini.operationState.OperationState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GeminiViewModel(
    val firebaseRepo: FirebaseRepo
) :
    ViewModel() {

    private var generativeModel: GenerativeModel

    private var chatRecord: MutableStateFlow<List<Chat>> = MutableStateFlow(emptyList())
    val chats = chatRecord.asStateFlow()

    private var operationStateFlow = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = operationStateFlow.asStateFlow()

    var enabled = mutableStateOf(true)
        private set

    init {
        generativeModel = GenerativeModel(
            modelName = "gemini-1.0-pro",
            apiKey = BuildConfig.API_KEY,
        )

    }

    fun raiseQuery(chatId: String, userPrompt: String) {
        enabled.value = false
        viewModelScope.launch(Dispatchers.IO) {
            operationStateFlow.emit(OperationState.Performing)
            try {
                val history = provideChatHistory(chatRecord.value)
                val modelChat = generativeModel.startChat(
                    history = history
                )
                val response = modelChat.sendMessage(userPrompt)
                val chat = Chat(
                    id = generateUniqueKey(),
                    userPrompt = userPrompt,
                    modelReply = response.text?.replace("**","\"")
                    ?.replace("*", "\n•") ?: "No Output"
                )
                firebaseRepo.addChat(
                    chatId,
                    chat
                )
                operationStateFlow.emit(OperationState.Done)
            } catch (e: Exception) {
                operationStateFlow.value =
                    OperationState.Error(e.message ?: "Some error while adding")
            } finally {
                enabled.value = true
            }
        }


    }

    fun listenChanges(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.listenData(chatId).collectLatest { messages ->
                val value = messages.sortedBy {
                    it.id
                }
                chatRecord.emit(value)
                Log.d("chat", value.toString())
                operationStateFlow.emit(OperationState.Idle)
            }
        }
    }


    fun deleteChat(chatId: String, id: String) {
        try {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                operationStateFlow.emit(OperationState.Performing)
                firebaseRepo.deleteChat(
                    chatId = chatId,
                    id = id
                )
                operationStateFlow.emit(OperationState.Done)
            }
        } catch (e: Exception) {
            Log.d("Delete", e.message ?: "Error")
            operationStateFlow.value = OperationState.Error(e.message ?: "Error While Deleting")
        }
    }


    fun errorOperationStateRelax() {
        viewModelScope.launch {
            operationStateFlow.emit(OperationState.Idle)
        }
    }


    private fun provideChatHistory(chats: List<Chat>): List<Content> {
        val list: MutableList<Content> = mutableListOf()
        chats.forEach { chat ->
            list += content("user") { text(chat.userPrompt)  }
            list += content("model") {text(chat.modelReply)}
        }

        return list.toList().ifEmpty { emptyList() }
    }

    private fun generateUniqueKey(): String =
        System.currentTimeMillis().toString()

}

