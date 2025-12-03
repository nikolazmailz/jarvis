package ru.jarvis.infra.sheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.jarvis.application.DialogService
import ru.jarvis.domain.queue.MessageQueueRepository
import ru.jarvis.domain.queue.MessageStatus

@Component
class MessageQueueScheduler(
    private val messageQueueRepository: MessageQueueRepository,
    private val dialogService: DialogService
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 500)
    fun pollQueue() {
        runBlocking {
            if (messageQueueRepository.existsByStatus(MessageStatus.PROCESSING)) {
                return@runBlocking
            }

            val pendingEntry = messageQueueRepository.findFirstByStatusOrderByCreatedAtAsc(MessageStatus.NEW)
            if (pendingEntry == null) {
                return@runBlocking
            }

            val processingEntry = messageQueueRepository.save(pendingEntry.copy(status = MessageStatus.PROCESSING))

            try {
                dialogService.processQueuedMessage(processingEntry)
                messageQueueRepository.save(processingEntry.copy(status = MessageStatus.SUCCESSFUL))
            } catch (ex: Exception) {
                log.error(ex) { "Failed to process queued message ${processingEntry.id}" }
                messageQueueRepository.save(processingEntry.copy(status = MessageStatus.FAILED))
            }
        }
    }
}