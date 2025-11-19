package ru.jarvis.infra.openai.dto

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)
