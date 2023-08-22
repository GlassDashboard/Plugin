package me.santio.glass.common.models.audit

import me.santio.glass.common.database.Database

data class Audit(
    val user: String,
    val message: String,
    val timestamp: Long
) {

    fun commit() {
        me.santio.glass.common.database.Database.connection.prepareStatement(
            "INSERT INTO audit_log(user, message, timestamp) VALUES(?, ?, ?);"
        ).apply {
            setString(1, user)
            setString(2, message)
            setString(3, timestamp.toString())
        }.execute()
    }

}
