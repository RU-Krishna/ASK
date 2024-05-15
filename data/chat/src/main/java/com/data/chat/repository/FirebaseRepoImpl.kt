package com.data.chat.repository

import com.data.chat.dao.FirebaseDAO
import com.data.model.firebaseData.Chat
import com.data.model.repo.FirebaseRepo
import kotlinx.coroutines.flow.Flow

class FirebaseRepoImpl(
    private val firebaseDAO: FirebaseDAO
): FirebaseRepo {

    override suspend fun addChat(chatId: String, chat: Chat) {
        firebaseDAO.addChat(chatId = chatId, chat = chat)
    }

    override fun createReference(path: String) {
        firebaseDAO.createReference(path)
    }

    override suspend fun listenData(chatId: String): Flow<List<Chat>> {
        return firebaseDAO.listenData(chatId = chatId)
    }

    override suspend fun deleteChat(chatId: String, id: String) {
        firebaseDAO.deleteChat(chatId, id = id)
    }
}