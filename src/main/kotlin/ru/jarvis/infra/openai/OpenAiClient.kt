package ru.jarvis.infra.openai

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.jarvis.infra.openai.dto.ChatCompletionRequest
import ru.jarvis.infra.openai.dto.ChatCompletionResponse
import ru.jarvis.infra.openai.dto.ChatMessage

@Component
class OpenAiClient(
    @Qualifier("openaiWebClient")
    private val webClient: WebClient
) {

    private val log = KotlinLogging.logger {}

    suspend fun requestChatCompletion(prompt: String): String {
        val request = ChatCompletionRequest(
            model = MODEL_3_5_TURBO,
            messages = listOf(
                ChatMessage(role = "system", content = "You are a helpful assistant."),
                ChatMessage(role = "user", content = prompt)
            )
        )

        log.debug { "Sending OpenAI chat completion request: $request" }

        val response = webClient.post()
            .uri("/chat/completions")
            .bodyValue(request)
            .retrieve()
            .awaitBody<ChatCompletionResponse>()

        log.debug { "Received OpenAI chat completion response: $response" }

        return response.choices.firstOrNull()?.message?.content
            ?: error("OpenAI response did not contain choices")
    }

    private companion object {
        private const val MODEL_3_5_TURBO = "gpt-3.5-turbo"
    }
}