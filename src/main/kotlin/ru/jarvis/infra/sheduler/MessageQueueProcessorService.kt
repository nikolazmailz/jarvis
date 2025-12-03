package ru.jarvis.infra.sheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.jarvis.application.AuditService
import ru.jarvis.application.DialogService
import ru.jarvis.domain.audit.LogErrorStageEnum
import ru.jarvis.domain.queue.MessageStatus
import ru.jarvis.infra.repo.MessageQueueRepository
import java.time.Instant

@Service
class MessageQueueProcessorService(
    private val messageQueueRepository: MessageQueueRepository,
    private val dialogService: DialogService,
    private val auditService: AuditService
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    suspend fun processPendingMessages(batchSize: Int) {
        val queuedEntries = messageQueueRepository.fetchBatchAndMarkProcessing(batchSize)
        if (queuedEntries.isEmpty()) {
            return
        }

        queuedEntries.forEach { entry ->
            try {
                dialogService.processQueuedMessage(entry)
                messageQueueRepository.save(
                    entry.copy(
                        status = MessageStatus.SUCCESSFUL,
                        updatedAt = Instant.now()
                    )
                )
            } catch (ex: Exception) {
                log.error(ex) { "Failed to process queued message ${entry.id}" }
                auditService.logErrorStage(
                   entry = entry,
                   exception = ex,
                   correlationId = entry.id,
                   stage = LogErrorStageEnum.QUEUE_PROCESSING
                )
                messageQueueRepository.save(
                    entry.copy(
                        status = MessageStatus.FAILED,
                        updatedAt = Instant.now()
                    )
                )
            }
        }
    }
}

