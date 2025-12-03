package ru.jarvis.infra.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.jarvis.domain.audit.MessageAudit
import java.util.UUID

interface MessageAuditRepository : CoroutineCrudRepository<MessageAudit, UUID>

