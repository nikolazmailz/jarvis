package ru.jarvis.domain.audit

enum class LogErrorStageEnum(
    val source: AuditSource,
    val direction: AuditDirection
) {
    OPENAI_REQUEST(
        source = AuditSource.OPENAI,
        direction = AuditDirection.OUTBOUND
    ),
    TELEGRAM_SEND_MESSAGE(
        source = AuditSource.TELEGRAM,
        direction = AuditDirection.OUTBOUND
    ),
    QUEUE_PROCESSING(
        source = AuditSource.SYSTEM,
        direction = AuditDirection.OUTBOUND
    )
}

