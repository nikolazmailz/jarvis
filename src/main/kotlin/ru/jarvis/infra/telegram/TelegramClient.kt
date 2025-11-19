package ru.jarvis.infra.telegram

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.jarvis.infra.telegram.dto.SendMessageRequest
import ru.jarvis.infra.telegram.dto.TelegramApiResponse

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

    private val webClient = webClientBuilder
        .baseUrl(API_URL)
        .build()

    /**
     * Отправляет простое текстовое сообщение в чат.
     */
    suspend fun sendMessage(
        chatId: Long,
        text: String,
    ) {
        val token = botToken.takeIf { it.isNotBlank() }
            ?: error("Telegram bot token is not configured")

        val request = SendMessageRequest(
            chatId = chatId,
            text = text
        )

        log.debug { "Sending Telegram message: $request" }

        webClient.post()
            .uri("$token/sendMessage")
            .bodyValue(request)
            .exchangeToMono { resp ->
                if (resp.statusCode().is2xxSuccessful) {
                    resp.bodyToMono(TelegramApiResponse::class.java)
                        .flatMap { raw ->
                            @Suppress("UNCHECKED_CAST")
                            val tg = raw as TelegramApiResponse<*>
                            if (tg.ok) Mono.just(Unit)
                            else Mono.error(IllegalStateException("Telegram error: ${tg.description ?: "unknown"}"))
                        }
                } else {
                    resp.bodyToMono(String::class.java)
                        .defaultIfEmpty("<empty body>")
                        .flatMap { body ->
                            val code = resp.statusCode().value()
                            log.warn { "Telegram HTTP $code: $body" }
                            Mono.error(IllegalStateException("HTTP $code from Telegram: $body"))
                        }
                }
            }
    }

    private companion object {
        private const val API_URL = "https://api.telegram.org/bot"
    }
}