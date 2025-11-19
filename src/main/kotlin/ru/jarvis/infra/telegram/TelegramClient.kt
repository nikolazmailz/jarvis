package ru.jarvis.infra.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.jarvis.domain.telegram.TelegramMessage
import ru.jarvis.infra.telegram.dto.SendMessageRequest
import ru.jarvis.infra.telegram.dto.TelegramApiResponse
import ru.jarvis.infra.telegram.dto.TelegramClientException

/**
 * HTTP-клиент для работы с Telegram Bot API.
 */
@Component
class TelegramClient(
    @Qualifier("telegramWebClient")
    private val webClient: WebClient,
    @Value("\${telegram.bot-token:}")
    private val botToken: String
) {

    private val log = KotlinLogging.logger {}

    /**
     * Отправляет простое текстовое сообщение в чат.
     */
    suspend fun sendMessage(
        chatId: Long,
        text: String,
    ): TelegramMessage {
        val token = botToken.takeIf { it.isNotBlank() }
            ?: error("Telegram bot token is not configured")

        val request = SendMessageRequest(
            chatId = chatId,
            text = text
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

}