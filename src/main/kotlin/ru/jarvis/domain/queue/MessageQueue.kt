package ru.jarvis.domain.queue

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("message_queue")
data class MessageQueue(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column("chat_id")
    val chatId: Long,
    @Column("message_text")
    val messageText: String,
    val status: MessageStatus = MessageStatus.NEW,
    val origin: MessageOrigin = MessageOrigin.USER,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("scheduled_at")
    val scheduledAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)

