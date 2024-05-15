package com.example.modelsettings.repo

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.data.model.settings.ModelSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


const val preferences = "modelSettings"
class SettingRepositoryImpl(
    private val context: Context
) {

    private val prefDataStore: DataStore<Preferences> = context.dataStore

    private object PreferencesKeys  {
        val temperature = floatPreferencesKey("temperature")
        val topP = floatPreferencesKey("topP")
        val topK = intPreferencesKey("topK")

        val harassment = stringPreferencesKey("harassment")
        val hateSpeech = stringPreferencesKey("hateSpeech")
        val sexuallyContent = stringPreferencesKey("sexuallyContent")
        val dangerous = stringPreferencesKey("dangerous")

    }


    suspend fun updateModalSettings(
        temperature: Float,
        topP: Float,
        topK: Int,
        harassment: String,
        hateSpeech: String,
        sexualContent: String,
        dangerous: String
    ) {
        prefDataStore.edit { pref ->
            pref[PreferencesKeys.temperature] = temperature
            pref[PreferencesKeys.topP] = topP
            pref[PreferencesKeys.topK] = topK
            pref[PreferencesKeys.harassment] = harassment
            pref[PreferencesKeys.hateSpeech] = hateSpeech
            pref[PreferencesKeys.sexuallyContent] = sexualContent
            pref[PreferencesKeys.dangerous] = dangerous
        }
    }


    fun refreshSettings(): Flow<ModelSettings> = prefDataStore.data
        .map {  pref ->
            ModelSettings(
                temperature = pref[PreferencesKeys.temperature]?:0.9f,
                topK = pref[PreferencesKeys.topK]?:50,
                topP = pref[PreferencesKeys.topP]?:0.9f,
                harassment = pref[PreferencesKeys.harassment]?:"BlockNone",
                hateSpeech = pref[PreferencesKeys.hateSpeech]?:"BlockNone",
                sexualContent = pref[PreferencesKeys.sexuallyContent]?:"BlockNone",
                dangerous = pref[PreferencesKeys.dangerous]?:"BlockNone"
            )
        }
        .catch {
            Toast
                .makeText(
                    context,
                    "Error while loading",
                    LENGTH_SHORT
                )
                .show()
        }

}

private val Context.dataStore by preferencesDataStore(
    name = preferences
)