package ru.jarvis.infra.openai.dto

data class ChatMessage(
    val role: String,
    val content: String
)
