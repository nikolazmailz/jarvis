package ru.jarvis.infra.telegram.dto

data class TelegramApiResponse<T>(
    val ok: Boolean,
    val result: T? = null,
    val description: String? = null
)