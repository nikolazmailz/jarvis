package ru.jarvis.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import ru.jarvis.domain.queue.Message
import ru.jarvis.domain.audit.AuditDirection
import ru.jarvis.domain.audit.AuditSource
import ru.jarvis.domain.telegram.TelegramMessage
import ru.jarvis.infra.openai.OpenAiClient
import ru.jarvis.infra.repo.MessageQueueRepository
import ru.jarvis.infra.telegram.TelegramClient

/**
 * Сохраняет входящие Telegram-сообщения в очередь и обрабатывает их асинхронно.
 */
@Service
class DialogService(
    private val telegramClient: TelegramClient,
    private val openAiClient: OpenAiClient,
    private val messageQueueRepository: MessageQueueRepository,
    private val auditService: AuditService
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

        val entry = Message(chatId = message.chat.id, messageText = text)
        auditService.logIncomingMessage(message, entry.id)
        messageQueueRepository.save(entry)
        log.info { "Queued Telegram message ${message.messageId} for chat ${message.chat.id}" }
    }

    /**
     * Запрашивает ответ LLM, логирует готовый текст и отправляет его в чат.
     */
    suspend fun processQueuedMessage(entry: Message) {
        val correlationId = entry.id
        auditService.logAiRequest(entry.chatId, entry.messageText, correlationId)

        val aiResponse = try {
            openAiClient.requestChatCompletion(entry.messageText)
        } catch (ex: Exception) {
            auditService.logError(
                chatId = entry.chatId,
                messageId = null,
                source = AuditSource.OPENAI,
                direction = AuditDirection.OUTBOUND,
                stage = "OPENAI_REQUEST",
                exception = ex,
                correlationId = correlationId,
                requestText = entry.messageText
            )
            throw ex
        }

        log.info { "LLM response for chat ${entry.chatId}: $aiResponse" }
        auditService.logAiResponse(entry.chatId, entry.messageText, aiResponse, correlationId)

        val telegramMessage = try {
            telegramClient.sendMessage(chatId = entry.chatId, text = aiResponse)
        } catch (ex: Exception) {
            auditService.logError(
                chatId = entry.chatId,
                messageId = null,
                source = AuditSource.TELEGRAM,
                direction = AuditDirection.OUTBOUND,
                stage = "TELEGRAM_SEND_MESSAGE",
                exception = ex,
                correlationId = correlationId,
                requestText = aiResponse
            )
            throw ex
        }

        auditService.logTelegramResponse(entry.chatId, telegramMessage, correlationId)
        log.info { "Sent Telegram message ${telegramMessage.messageId} to chat ${entry.chatId}" }
    }
}

