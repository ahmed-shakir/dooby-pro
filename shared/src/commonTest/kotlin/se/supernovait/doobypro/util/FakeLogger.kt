package se.supernovait.doobypro.util

import se.supernovait.app.core.domain.logging.LogLevel
import se.supernovait.app.core.domain.logging.Logger

/**
 * Fake implementation of [Logger] for unit tests.
 */
class FakeLogger : Logger {
    val logs = mutableListOf<LogEntry>()

    data class LogEntry(
        val level: LogLevel,
        val message: String,
        val throwable: Throwable? = null,
        val tag: String? = null
    )

    override fun trace(message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(LogLevel.TRACE, message, throwable, tag))
    }

    override fun debug(message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(LogLevel.DEBUG, message, throwable, tag))
    }

    override fun info(message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(LogLevel.INFO, message, throwable, tag))
    }

    override fun warn(message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(LogLevel.WARN, message, throwable, tag))
    }

    override fun error(message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(LogLevel.ERROR, message, throwable, tag))
    }

    override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) {
        logs.add(LogEntry(level, message, throwable, tag))
    }
}
