package com.data.model.repo

interface AuthRepo {

    fun signUp(email: String, password: String)

    fun signIn(email: String, password: String)

    fun changePassword(email: String)

    fun updateEmail()

    fun updatePhoto()

    fun updateDisplayName()

    fun signOut()

}