package com.data.record.repository

import com.data.model.firebaseData.ChatRecord
import com.data.model.repo.ChatRecordRepo
import com.data.record.dao.ChatRecordDAO
import kotlinx.coroutines.flow.Flow

class ChatRecordRepoImpl(
    private val chatRecordDAO: ChatRecordDAO
): ChatRecordRepo {

    override suspend fun addRecord(record: ChatRecord) {
        chatRecordDAO.addRecord(record)
    }

    override suspend fun deleteRecord(recordId: String) {
        chatRecordDAO.deleteRecord(recordId)
    }

    override suspend fun updateRecordName(recordId: String, recordName: String) {
        chatRecordDAO.updateRecordName(recordId,recordName)
    }

    override suspend fun fetchRecords(): Flow<List<ChatRecord>> {
        return chatRecordDAO.fetchRecords()
    }

    override fun createReference(path: String) {
        chatRecordDAO.createReference(path)
    }
}