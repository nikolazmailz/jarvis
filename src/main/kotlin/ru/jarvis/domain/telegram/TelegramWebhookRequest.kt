package ru.jarvis.domain.telegram

import com.fasterxml.jackson.annotation.JsonProperty

// todo перенести в controller ?
/**
 * DTO, описывающий обновление Telegram, которое мы получаем через webhook.
 */
data class TelegramWebhookRequest(
    @JsonProperty("update_id")
    val updateId: Long,
    val message: TelegramMessage? = null,
    @JsonProperty("callback_query")
    val callbackQuery: TelegramCallbackQuery? = null
)

/**
 * Представляет стандартное сообщение Telegram.
 */
data class TelegramMessage(
    val messageId: Long,
    val from: TelegramUser?,
    val chat: TelegramChat,
    val date: Long,
    val text: String? = null
)

/**
 * Содержит данные о пользователе, который отправил сообщение.
 */
data class TelegramUser(
    val id: Long,
    @JsonProperty("is_bot")
    val isBot: Boolean,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String? = null,
    val username: String? = null,
    @JsonProperty("language_code")
    val languageCode: String? = null
)

/**
 * Описывает чат, в который пришло сообщение.
 */
data class TelegramChat(
    val id: Long,
    val type: String,
    val title: String? = null,
    val username: String? = null,
    @JsonProperty("first_name")
    val firstName: String? = null,
    @JsonProperty("last_name")
    val lastName: String? = null
)

/**
 * Представляет payload callback-запроса, который Telegram отправляет на webhook.
 */
data class TelegramCallbackQuery(
    val id: String,
    val from: TelegramUser,
    val message: TelegramMessage? = null,
    val data: String? = null
)
