package ru.jarvis.domain.audit

enum class AuditSource {
    TELEGRAM,
    OPENAI,
    SYSTEM
}

enum class AuditDirection {
    INBOUND,
    OUTBOUND
}

enum class AuditStatus {
    RECEIVED,
    SUCCESS,
    ERROR
}

