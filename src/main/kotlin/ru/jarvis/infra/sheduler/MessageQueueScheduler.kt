package ru.jarvis.infra.sheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MessageQueueScheduler(
    private val messageQueueProcessorService: MessageQueueProcessorService,
    @Value("\${dialog.queue.batch-size:10}")
    private val batchSize: Int
) {

    private val log = KotlinLogging.logger {}
    private val pollingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Scheduled(fixedDelayString = "\${dialog.queue.poll-interval-ms:500}")
    fun pollQueue() {
        pollingScope.launch {
            log.debug { "Polling queue for up to $batchSize messages" }
            try {
                messageQueueProcessorService.processPendingMessages(batchSize)
            } catch (ex: Throwable) {
                log.error(ex) { "Message queue processing failed" }
            }
        }
    }
}