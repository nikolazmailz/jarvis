package ru.jarvis.application

import org.springframework.stereotype.Service
import ru.jarvis.domain.telegram.TelegramMessage
import ru.jarvis.infra.telegram.TelegramClient

/**
 * Принимает сообщения Telegram и отвечает на них простым echo-текстом.
 */
@Service
class DialogService(
    private val telegramClient: TelegramClient
) {

    /**
     * Формирует сообщение вида "Пользователь <name> (<id>) спросил: <text>"
     * и отправляет его в исходный чат.
     */
    suspend fun directAnswer(message: TelegramMessage) {
        val user = message.from
            ?: error("Cannot send direct answer: sender is missing in Telegram message")

        val formattedText = buildString {
            append("Пользователь ")
            append(user.firstName)
            user.lastName?.let { append(' ').append(it) }
            append(" (id=")
            append(user.id)
            append(") спросил: ")
            append(message.text ?: "")
        }

        telegramClient.sendMessage(
            chatId = message.chat.id,
            text = formattedText
        )
    }
}
