package ru.jarvis.domain.audit

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("message_audit")
data class MessageAudit(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column("chat_id")
    val chatId: Long,
    @Column("message_id")
    val messageId: Long? = null,
    val direction: String,
    val source: String,
    val status: String,
    @Column("request_text")
    val requestText: String? = null,
    @Column("response_text")
    val responseText: String? = null,
    @Column("error_type")
    val errorType: String? = null,
    @Column("error_message")
    val errorMessage: String? = null,
    @Column("correlation_id")
    val correlationId: UUID? = null,
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)

