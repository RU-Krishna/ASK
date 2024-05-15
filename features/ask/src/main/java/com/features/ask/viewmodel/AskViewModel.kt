package com.features.ask.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.firebaseData.Chat
import com.data.model.repo.FirebaseRepo
import com.data.model.settings.ModelSettings
import com.example.modelsettings.repo.SettingRepositoryImpl
import com.features.ask.operationState.OperationState
import com.features.gemini.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AskViewModel(
    val firebaseRepo: FirebaseRepo,
    private val settingsRepo: SettingRepositoryImpl
) :
    ViewModel() {


    private val modelSettings: Flow<ModelSettings> = settingsRepo.refreshSettings()

    val modelSettingsFlow = modelSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ModelSettings(
            temperature = 0.9f,
            topK = 56,
            topP = .9f,
            harassment = BlockThreshold.MEDIUM_AND_ABOVE.name,
            hateSpeech = BlockThreshold.MEDIUM_AND_ABOVE.name,
            sexualContent = BlockThreshold.MEDIUM_AND_ABOVE.name,
            dangerous = BlockThreshold.MEDIUM_AND_ABOVE.name)
    )


    private lateinit var safetySettings: List<SafetySetting>

    private var generationConfig = generationConfig {
        this.temperature = 0.9f
        this.topP = 0.9f
        this.topK = 56

    }


    private var textGenerativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.0-pro",
        apiKey = BuildConfig.API_KEY,
    )

    private var multiModalGenerativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.API_KEY
    )

    private var chatRecord: MutableStateFlow<List<Chat>> = MutableStateFlow(emptyList())
    val chats = chatRecord.asStateFlow()

    private var operationStateFlow = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = operationStateFlow.asStateFlow()

    var enabled = mutableStateOf(true)
        private set

    fun raiseQuery(chatId: String, userPrompt: String) {
        enabled.value = false
        viewModelScope.launch(Dispatchers.IO) {
            operationStateFlow.emit(OperationState.Performing)
            try {
                val history = provideChatHistory(chatRecord.value)
                val modelChat = textGenerativeModel.startChat(
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

    fun raiseMultiModalQuery(chatId: String, userPrompt: String, images: List<Bitmap>) {
        enabled.value = false
        viewModelScope.launch(Dispatchers.IO) {
            operationStateFlow.emit(OperationState.Performing)
            try {
                val response = multiModalGenerativeModel.generateContent(
                    content("user") {
                        images.forEach {
                            image(it)
                        }
                        text(userPrompt)
                    }
                )
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

    fun updateModelSettings(
        model: ModelSettings
    ) {

        viewModelScope
            .launch(Dispatchers.IO) {
                settingsRepo
                    .updateModalSettings(
                        temperature = model.temperature,
                        topK = model.topK,
                        topP = model.topP,
                        harassment = model.harassment,
                        hateSpeech = model.hateSpeech,
                        sexualContent = model.sexualContent,
                        dangerous = model.dangerous
                    )
                updateSafetySettings(model)
                updateGenerationConfig(model)
                textGenerativeModel = GenerativeModel(
                    modelName = "gemini-1.0-pro",
                    apiKey = BuildConfig.API_KEY,
                    safetySettings = safetySettings,
                    generationConfig = generationConfig
                )
                multiModalGenerativeModel = GenerativeModel(
                    modelName = "gemini-pro-vision",
                    apiKey = BuildConfig.API_KEY,
                    safetySettings = safetySettings,
                    generationConfig = generationConfig
                )

                textGenerativeModel.generationConfig.let {
                    Log.d("modelX", "" + it?.temperature!! + " " + it.topK!! + " " + it.topP!! )
                }

                textGenerativeModel.safetySettings?.forEach {
                    Log.d("modelX", it.harmCategory.toString() + "->" + it.threshold.toString())
                }

            }

    }



    private fun updateSafetySettings(
        model: ModelSettings
    ) {
        safetySettings = listOf(
            SafetySetting(harmCategory = HarmCategory.HARASSMENT, threshold = BlockThreshold.valueOf(model.harassment)),
            SafetySetting(harmCategory = HarmCategory.HATE_SPEECH, threshold = BlockThreshold.valueOf(model.hateSpeech)),
            SafetySetting(harmCategory = HarmCategory.SEXUALLY_EXPLICIT, threshold = BlockThreshold.valueOf(model.sexualContent)),
            SafetySetting(harmCategory = HarmCategory.DANGEROUS_CONTENT, threshold = BlockThreshold.valueOf(model.dangerous))
        )
    }


    private fun updateGenerationConfig(
        model: ModelSettings
    ) {
        generationConfig = generationConfig {
            this.temperature = model.temperature
            this.topP = model.topP
            this.topK = model.topK
        }
    }

    private fun generateUniqueKey(): String =
        System.currentTimeMillis().toString()


}




