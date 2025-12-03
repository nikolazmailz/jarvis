package ru.jarvis.infra.sheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.jarvis.application.DialogService
import ru.jarvis.domain.queue.MessageQueueRepository
import ru.jarvis.domain.queue.MessageStatus
import java.time.Instant

@Service
class MessageQueueProcessorService(
    private val messageQueueRepository: MessageQueueRepository,
    private val dialogService: DialogService
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

