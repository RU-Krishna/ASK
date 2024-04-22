package com.example.gemini.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.firebaseData.ChatRecord
import com.data.model.repo.ChatRecordRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class RecordViewModel : ViewModel() {

    var currentChatId = mutableStateOf("")
    private set

    lateinit var recordRepo: ChatRecordRepo

    var isLandingPage = mutableStateOf(true)
        private set

    private val chatRecord = MutableStateFlow<List<ChatRecord>>(emptyList())
    val chatRecords = chatRecord.asStateFlow()

    fun initRecordRepo(chatRecordRepo: ChatRecordRepo) {
        recordRepo = chatRecordRepo
    }

    fun modifyLandingPage() {
            addRecord(currentChatId.value)
            Log.d("CurrentIdX", currentChatId.value + " "+ Calendar.getInstance().time.toString())

    }

    private fun addRecord(chatId: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                recordRepo.addRecord(
                    ChatRecord(
                        chatId = chatId
                    )
                )
            }
        } catch(e: Exception) {
            Log.e("Exception", e.message?:"Something Unexpected Occur")
        }
        finally {
        }
    }

    fun showRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                recordRepo.fetchRecords().collectLatest {
                    chatRecord.emit(it)
                }
                delay(2000)
            }
        }
    }

    fun deleteRecord(chatId: String) {
       viewModelScope.launch(Dispatchers.IO) {
           recordRepo.deleteRecord(
               recordId = chatId
           )
       }
    }

    fun changeCurrentChat(recordId: String) {
            isLandingPage.value = false
            currentChatId.value = recordId
    }

    fun noRecordOrEmptyChat() {
        isLandingPage.value = true
    }








}