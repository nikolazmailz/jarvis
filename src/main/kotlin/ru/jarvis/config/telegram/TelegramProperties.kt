package ru.jarvis.config.telegram

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("telegram")
data class TelegramProperties(
    var botToken: String = "REPLACE_ME",
    var webhookSecret: String? = null
)