package com.data.model.settings


data class ModelSettings(
    var temperature: Float = 0.9f,
    var topK: Int = 56,
    var topP: Float = 0.9f,
    var harassment: String = "",
    var hateSpeech: String = "",
    var sexualContent: String = "",
    var dangerous: String = ""
)
