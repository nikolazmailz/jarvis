package ru.jarvis.infra

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.jarvis.domain.telegram.TelegramMessage

/**
 * HTTP-клиент для работы с Telegram Bot API.
 */
@Component
class TelegramClient(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${telegram.bot-token:}")
    private val botToken: String
) {

    private val log = KotlinLogging.logger {}

    private val webClient: WebClient by lazy {
        webClientBuilder
            .baseUrl(API_URL)
            .build()
    }

    /**
     * Отправляет простое текстовое сообщение в чат.
     */
    suspend fun sendMessage(
        chatId: Long,
        text: String,
        parseMode: String? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null
    ): TelegramMessage {
        val token = botToken.takeIf { it.isNotBlank() }
            ?: error("Telegram bot token is not configured")

        val request = SendMessageRequest(
            chatId = chatId,
            text = text,
            parseMode = parseMode,
            disableWebPagePreview = disableWebPagePreview,
            disableNotification = disableNotification
        )

        log.debug { "Sending Telegram message: $request" }

        val response = webClient.post()
            .uri("/bot$token/sendMessage")
            .bodyValue(request)
            .retrieve()
            .awaitBody<TelegramApiResponse<TelegramMessage>>()

        if (!response.ok || response.result == null) {
            throw TelegramClientException(
                "Failed to send Telegram message: ${response.description ?: "unknown error"}"
            )
        }

        return response.result
    }

    private data class SendMessageRequest(
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

    private data class TelegramApiResponse<T>(
        val ok: Boolean,
        val result: T? = null,
        val description: String? = null
    )

    class TelegramClientException(message: String) : RuntimeException(message)

    private companion object {
        private const val API_URL = "https://api.telegram.org"
    }
}
