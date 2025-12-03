package ru.jarvis.domain.queue

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface MessageQueueRepository : CoroutineCrudRepository<MessageQueueEntry, UUID> {
    suspend fun existsByStatus(status: MessageStatus): Boolean
    suspend fun findFirstByStatusOrderByCreatedAtAsc(status: MessageStatus): MessageQueueEntry?
}

