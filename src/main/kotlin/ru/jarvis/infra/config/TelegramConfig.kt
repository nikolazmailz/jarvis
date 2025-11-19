package ru.jarvis.infra.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class TelegramConfig {

    @Bean
    fun telegramWebClient(webClientBuilder: WebClient.Builder): WebClient {
        return webClientBuilder
            .baseUrl(API_URL)
            .build()
    }

    private companion object {
        private const val API_URL = "https://api.telegram.org"
    }
}
