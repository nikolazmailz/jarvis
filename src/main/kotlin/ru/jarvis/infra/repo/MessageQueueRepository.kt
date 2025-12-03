package ru.jarvis.infra.repo

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import ru.jarvis.domain.queue.Message
import ru.jarvis.domain.queue.MessageStatus
import java.util.UUID

interface MessageQueueRepository : CoroutineCrudRepository<Message, UUID> {
    suspend fun existsByStatus(status: MessageStatus): Boolean
    suspend fun findFirstByStatusOrderByCreatedAtAsc(status: MessageStatus): Message?

    @Query(
        """
        WITH cte AS (
            SELECT id FROM dialog_queue
            WHERE status = 'NEW' AND scheduled_at <= now()
            ORDER BY scheduled_at ASC
            LIMIT :batch
            FOR UPDATE SKIP LOCKED
        )
        UPDATE dialog_queue q
        SET status = 'PROCESSING', updated_at = now()
        WHERE q.id IN (SELECT id FROM cte)
        RETURNING q.*
        """
    )
    suspend fun fetchBatchAndMarkProcessing(@Param("batch") batch: Int): List<Message>
}