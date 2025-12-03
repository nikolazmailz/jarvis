package ru.jarvis.controller

import com.fasterxml.jackson.annotation.JsonProperty
import ru.jarvis.domain.telegram.TelegramCallbackQuery
import ru.jarvis.domain.telegram.TelegramMessage

data class TelegramWebhookRequest(
    @JsonProperty("update_id")
    val updateId: Long,
    val message: TelegramMessage? = null,
    @JsonProperty("callback_query")
    val callbackQuery: TelegramCallbackQuery? = null
)