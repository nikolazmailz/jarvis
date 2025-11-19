package ru.jarvis.config.openai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("proxy")
data class ProxyProperties(
    val host: String = "proxyHost",
    val port: String = "0",
    val user: String = "proxyUsername",
    val password: String = "proxyPassword",
)
