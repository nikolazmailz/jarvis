package ru.jarvis.domain.queue

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("message_queue")
data class MessageQueueEntry(
    @Id
    val id: UUID = UUID.randomUUID(),
    val chatId: Long,
    val messageText: String,
    val status: MessageStatus = MessageStatus.NEW,
    val origin: MessageOrigin = MessageOrigin.USER,
    val createdAt: Instant = Instant.now()
)

