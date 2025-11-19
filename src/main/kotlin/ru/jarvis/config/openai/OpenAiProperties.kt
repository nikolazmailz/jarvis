package ru.jarvis.config.openai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("openai")
data class OpenAiProperties(
    val token: String = "REPLACE_ME",
)