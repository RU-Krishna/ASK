package com.data.chat.dao

import com.data.model.firebaseData.Chat
import kotlinx.coroutines.flow.Flow

interface FirebaseDAO {

    //Add the chat to the firebase.
    suspend fun addChat(chatId: String = "", chat: Chat)

    //Update the chat to the firebase.
    fun createReference(path: String)

    //Fetch the required chats.
    suspend fun listenData(chatId: String = ""): Flow<List<Chat>>

    //Delete the required chat.
    suspend fun deleteChat(chatId: String,id: String)

}
