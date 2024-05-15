package com.example.gemini

import android.app.Application
import android.util.Log
import com.data.chat.container.ChatContainer
import com.data.chat.container.FirebaseAppContainer
import com.data.record.container.ChatRecordContainer
import com.data.record.container.ChatRecordContainerImpl
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyApplication: Application() {

    lateinit var chatContainer: ChatContainer
    lateinit var recordContainer: ChatRecordContainer
    private lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        try {
            chatContainer = FirebaseAppContainer(
                firebaseDatabase
            )
            recordContainer = ChatRecordContainerImpl(
                firebaseDatabase
            )
        } catch(e: Exception) {
            Log.e("exception", e.message?:"")
        }
    }
}