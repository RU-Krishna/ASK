package com.data.model.repo

import com.data.model.firebaseData.ChatRecord
import kotlinx.coroutines.flow.Flow

interface ChatRecordRepo {

    suspend fun addRecord(record: ChatRecord)


    suspend fun deleteRecord(recordId: String)

    suspend fun updateRecordName(recordId: String, recordName: String)

    suspend fun fetchRecords(): Flow<List<ChatRecord>>

    fun createReference(path: String)

}