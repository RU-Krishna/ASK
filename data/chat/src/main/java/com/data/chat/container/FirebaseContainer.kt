package com.data.chat.container

import com.data.chat.dao.FirebaseDAO
import com.data.chat.repository.FirebaseRepoImpl
import com.data.model.firebaseData.Chat
import com.data.model.repo.FirebaseRepo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface ChatContainer {
    val firebaseRepo: FirebaseRepo
}


class FirebaseAppContainer(
    private var firebaseDatabase: FirebaseDatabase
) : ChatContainer {

    private lateinit var databaseReference: DatabaseReference


    private val firebaseDAO = object : FirebaseDAO {

        override suspend fun addChat(chatId: String, chat: Chat) {
            databaseReference
                .child(chatId)
                .child(chat.id)
                .setValue(chat)
                .addOnFailureListener { e ->
                    throw(e)
                }
        }

        override fun createReference(path: String) {
            databaseReference = firebaseDatabase.getReference(path)
        }

        override suspend fun listenData(chatId: String): Flow<List<Chat>> {

            return callbackFlow {
                val listener =
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val data = snapshot.getValue(object :
                                GenericTypeIndicator<Map<String, Chat>>() {})
                            val chats = data?.values?.toList() ?: emptyList()
                            try {
                                trySend(chats)
                            } catch (e: Exception) {
                                close(e)
                                throw(e)
                            } finally {
                                close()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            close(error.toException())
                            throw(error.toException())
                        }

                    }

                databaseReference.child(chatId).addValueEventListener(listener)

                awaitClose {
                    databaseReference.removeEventListener(listener)
                }


            }

        }

        override suspend fun deleteChat(chatId: String, id: String) {
            if(id.isNotEmpty()) {
                databaseReference
                    .child("$chatId/$id")
                    .removeValue()
                    .addOnFailureListener { e ->
                        throw (e)
                    }
            }
            else {
                databaseReference
                    .child(chatId)
                    .removeValue()
                    .addOnFailureListener { e ->
                        throw (e)
                    }

            }
        }
    }

    override val firebaseRepo: FirebaseRepo by lazy {
        FirebaseRepoImpl(firebaseDAO = firebaseDAO)
    }


}