package ru.jarvis.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import ru.jarvis.domain.queue.MessageQueueEntry
import ru.jarvis.domain.queue.MessageQueueRepository
import ru.jarvis.domain.telegram.TelegramMessage
import ru.jarvis.infra.openai.OpenAiClient
import ru.jarvis.infra.telegram.TelegramClient

/**
 * Сохраняет входящие Telegram-сообщения в очередь и обрабатывает их асинхронно.
 */
@Service
class DialogService(
    private val telegramClient: TelegramClient,
    private val openAiClient: OpenAiClient,
    private val messageQueueRepository: MessageQueueRepository
) {

    private val log = KotlinLogging.logger {}

    /**
     * Добавляет сообщение в очередь, если оно содержит текст.
     */
    suspend fun enqueueMessage(message: TelegramMessage) {
        val text = message.text
        if (text.isNullOrBlank()) {
            log.debug { "Skipping Telegram message ${message.messageId} because text is empty" }
            return
        }

        val entry = MessageQueueEntry(chatId = message.chat.id, messageText = text)
        messageQueueRepository.save(entry)
        log.info { "Queued Telegram message ${message.messageId} for chat ${message.chat.id}" }
    }

    /**
     * Запрашивает ответ LLM, логирует готовый текст и отправляет его в чат.
     */
    suspend fun processQueuedMessage(entry: MessageQueueEntry) {
        val aiResponse = openAiClient.requestChatCompletion(entry.messageText)
        log.info { "LLM response for chat ${entry.chatId}: $aiResponse" }
        telegramClient.sendMessage(chatId = entry.chatId, text = aiResponse)
    }
}

