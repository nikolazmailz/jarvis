package ru.jarvis.infra.telegram.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SendMessageRequest(
    val chat_id: Long,
    val text: String,
    val parse_mode: String? = null,
    val disable_web_page_preview: Boolean? = null,
    val disable_notification: Boolean? = null
)


