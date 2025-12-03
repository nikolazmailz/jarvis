package ru.jarvis.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import ru.jarvis.domain.audit.AuditDirection
import ru.jarvis.domain.audit.AuditSource
import ru.jarvis.domain.audit.AuditStatus
import ru.jarvis.domain.audit.LogErrorStageEnum
import ru.jarvis.domain.audit.MessageAudit
import ru.jarvis.domain.queue.Message
import ru.jarvis.domain.telegram.TelegramMessage
import ru.jarvis.infra.repo.MessageAuditRepository
import java.util.UUID

@Service
class AuditService(
    private val messageAuditRepository: MessageAuditRepository
) {

    private val log = KotlinLogging.logger {}

    suspend fun logIncomingMessage(message: TelegramMessage, correlationId: UUID) {
        val audit = MessageAudit(
            chatId = message.chat.id,
            messageId = message.messageId,
            direction = AuditDirection.INBOUND.name,
            source = AuditSource.TELEGRAM.name,
            status = AuditStatus.RECEIVED.name,
            requestText = message.text,
            correlationId = correlationId
        )

        persistAudit(audit, "incoming Telegram message")
    }

    suspend fun logAiRequest(chatId: Long, requestText: String, correlationId: UUID) {
        val audit = MessageAudit(
            chatId = chatId,
            direction = AuditDirection.OUTBOUND.name,
            source = AuditSource.OPENAI.name,
            status = AuditStatus.RECEIVED.name,
            requestText = requestText,
            correlationId = correlationId
        )

        persistAudit(audit, "OpenAI request")
    }

    suspend fun logAiResponse(
        chatId: Long,
        requestText: String,
        responseText: String,
        correlationId: UUID
    ) {
        val audit = MessageAudit(
            chatId = chatId,
            direction = AuditDirection.INBOUND.name,
            source = AuditSource.OPENAI.name,
            status = AuditStatus.SUCCESS.name,
            requestText = requestText,
            responseText = responseText,
            correlationId = correlationId
        )

        persistAudit(audit, "OpenAI response")
    }

    suspend fun logTelegramResponse(
        chatId: Long,
        telegramMessage: TelegramMessage,
        correlationId: UUID
    ) {
        val audit = MessageAudit(
            chatId = chatId,
            messageId = telegramMessage.messageId,
            direction = AuditDirection.OUTBOUND.name,
            source = AuditSource.TELEGRAM.name,
            status = AuditStatus.SUCCESS.name,
            requestText = telegramMessage.text,
            responseText = telegramMessage.text,
            correlationId = correlationId
        )

        persistAudit(audit, "Telegram send")
    }

    suspend fun logError(
        chatId: Long,
        messageId: Long?,
        source: AuditSource,
        direction: AuditDirection,
        stage: String,
        exception: Throwable,
        correlationId: UUID?,
        requestText: String? = null,
        responseText: String? = null
    ) {
        val audit = MessageAudit(
            chatId = chatId,
            messageId = messageId,
            direction = direction.name,
            source = source.name,
            status = AuditStatus.ERROR.name,
            requestText = requestText,
            responseText = responseText,
            errorType = exception::class.simpleName,
            errorMessage = buildErrorMessage(stage, exception),
            correlationId = correlationId ?: UUID.randomUUID()
        )

        persistAudit(audit, "error $stage")
    }

    suspend fun logErrorStage(
        entry: Message,
        exception: Throwable,
        correlationId: UUID?,
        stage: LogErrorStageEnum,
        requestText: String? = null,
        responseText: String? = null
    ) {
        logError(
            chatId = entry.chatId,
            messageId = null,
            source = stage.source,
            direction = stage.direction,
            stage = stage.name,
            exception = exception,
            correlationId = correlationId,
            requestText = requestText ?: entry.messageText,
            responseText = responseText
        )
    }

    private fun buildErrorMessage(stage: String, exception: Throwable): String {
        val trimmedMessage = exception.message?.takeUnless { it.isBlank() } ?: "no message"
        val stackTrace = exception.stackTraceToString().take(1024)
        return "[$stage] $trimmedMessage\n$stackTrace"
    }

    private suspend fun persistAudit(audit: MessageAudit, context: String) {
        try {
            messageAuditRepository.save(audit)
        } catch (ex: Exception) {
            log.warn(ex) { "Cannot persist audit record for $context" }
        }
    }
}

