package ru.jarvis.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import ru.jarvis.config.telegram.TelegramProperties

@Configuration
@EnableConfigurationProperties(TelegramProperties::class)
class PropertiesConfig