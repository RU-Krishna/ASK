package com.data.record.container

import com.data.model.firebaseData.ChatRecord
import com.data.model.repo.ChatRecordRepo
import com.data.record.dao.ChatRecordDAO
import com.data.record.repository.ChatRecordRepoImpl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface ChatRecordContainer {
    val chatRecordRepo: ChatRecordRepo
}

class ChatRecordContainerImpl(
    private val firebaseDatabase: FirebaseDatabase
): ChatRecordContainer {

    private lateinit var databaseReference: DatabaseReference


    private val chatRecordDAO = object: ChatRecordDAO {
        override suspend fun addRecord(record: ChatRecord) {
            databaseReference
                .child(record.chatId)
                .setValue(record)
                .addOnFailureListener { e ->
                    throw(e)
                }
        }

        override suspend fun deleteRecord(recordId: String) {
            databaseReference
                .child(recordId)
                .removeValue()
                .addOnFailureListener { e ->
                    throw(e)
                }
        }

        override suspend fun updateRecordName(recordId: String, recordName: String) {
            databaseReference
        .child(recordId).child("title").setValue(recordName)
                .addOnFailureListener { e ->
                    throw(e)
                }

        }

        override suspend fun fetchRecords(): Flow<List<ChatRecord>> {
            return callbackFlow {

                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = snapshot.getValue(object :
                            GenericTypeIndicator<Map<String, ChatRecord>>() {})
                        val record = data?.values?.toList() ?: emptyList()
                        try {
                            trySend(record)
                        } catch (e: Exception) {
                            close(e)
                            throw(e)
                        } finally {
                            close()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close()
                        throw(error.toException())
                    }

                }
                databaseReference.addValueEventListener(listener)
                awaitClose {
                    databaseReference.removeEventListener(listener)
                }
            }
        }

        override fun createReference(path: String) {
            databaseReference = firebaseDatabase.getReference(path)
        }

    }
    override val chatRecordRepo: ChatRecordRepo by lazy {
        ChatRecordRepoImpl(chatRecordDAO)
    }


}