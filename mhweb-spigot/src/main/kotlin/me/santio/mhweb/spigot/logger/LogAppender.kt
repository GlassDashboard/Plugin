package me.santio.mhweb.spigot.logger

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.logs.impl.ConsoleLog
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property

class LogAppender: AbstractAppender("Glass", null, null, false, Property.EMPTY_ARRAY) {
    init {
        super.start()
        (LogManager.getRootLogger() as Logger).addAppender(this)
    }

    fun close() {
        super.stop()
        (LogManager.getRootLogger() as Logger).removeAppender(this)
    }

    override fun append(e: LogEvent) {
        // Get bytes from log
        val message: String = if (e.message.throwable != null) {
            "${e.message.formattedMessage}\n${e.message.throwable.message}\n" +
            e.message.throwable.stackTrace.joinToString("\n") { it.toString() }
        } else String(e.message.formattedMessage.toByteArray(), Charsets.UTF_8)

        // Send log to glass
        Glass.sendLog(
            e.timeMillis.toString(),
            ConsoleLog(
                message,
                e.level.standardLevel.name,
            ),
            true
        )
    }
}