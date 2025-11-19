package ru.jarvis.infra.telegram.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SendMessageRequest(
    @JsonProperty("chat_id")
    val chatId: Long,
    val text: String,
    @JsonProperty("parse_mode")
    val parseMode: String? = null,
    @JsonProperty("disable_web_page_preview")
    val disableWebPagePreview: Boolean? = null,
    @JsonProperty("disable_notification")
    val disableNotification: Boolean? = null
)


