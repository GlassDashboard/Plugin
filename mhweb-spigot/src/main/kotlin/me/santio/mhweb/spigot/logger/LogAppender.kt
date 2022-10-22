package me.santio.mhweb.spigot.logger

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.logs.impl.ConsoleLog
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property

class LogAppender: AbstractAppender("MHWeb", null, null, false, Property.EMPTY_ARRAY) {
    init {
        super.start()
        (LogManager.getRootLogger() as Logger).addAppender(this)
    }

    fun close() {
        super.stop()
        (LogManager.getRootLogger() as Logger).removeAppender(this)
    }

    override fun append(e: LogEvent) {
        val message = if (e.message.throwable != null) e.message.throwable.message
        else e.message.formattedMessage

        Glass.sendLog(
            e.instant.epochMillisecond.toString(),
            ConsoleLog(
                message ?: "[Glass] Invalid log message provided",
                e.level.standardLevel.name,
            ),
            true
        )
    }
}