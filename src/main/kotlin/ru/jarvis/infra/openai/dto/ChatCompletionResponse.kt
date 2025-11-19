package ru.jarvis.infra.openai.dto

data class ChatCompletionResponse(
    val choices: List<ChatCompletionChoice> = emptyList()
)
