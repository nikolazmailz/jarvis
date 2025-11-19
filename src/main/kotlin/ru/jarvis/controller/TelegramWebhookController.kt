package ru.jarvis.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.jarvis.application.DialogService
import ru.jarvis.domain.telegram.TelegramWebhookRequest

/**
 * Контроллер, принимающий callback-запросы Telegram webhook.
 */
@RestController
@RequestMapping("/tg")
class TelegramWebhookController(
    private val dialogService: DialogService
) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/webhook")
    suspend fun handleWebhook(@RequestBody request: TelegramWebhookRequest): ResponseEntity<Unit> {
        log.info { "Received Telegram update: $request" }

        request.message?.let { dialogService.directAnswer(it) }

        return ResponseEntity.ok().build()
    }
}
