package me.santio.glass.common.database

import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


@Suppress("SqlNoDataSourceInspection")
object Database {

    lateinit var connection: Connection

    fun connect() {
        try {
            val path = me.santio.glass.common.GlassFileManager.DB_FILE
            path.mkdirs()

            val url = "jdbc:sqlite:${path.absolutePath}"
            me.santio.glass.common.database.Database.connection = DriverManager.getConnection(url)
            me.santio.glass.common.Glass.log("Successfully connected to database.")

            me.santio.glass.common.database.Database.initTables()
        } catch (e: SQLException) {
            me.santio.glass.common.Glass.log("[ERROR] Failed to establish connection to database.")
            println(e.message)
        }
    }

    private fun initTables() {
        me.santio.glass.common.database.Database.connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS plugins(
                    id INTEGER      PRIMARY KEY AUTO_INCREMENT,
                    file TEXT       NOT NULL,
                    checksum TEXT       NOT NULL
                );
            """.trimIndent()
        ).execute()

        me.santio.glass.common.database.Database.connection.prepareStatement(
            """
                CREATE TABLE IF NOT EXISTS audit_log(
                    id INTEGER      PRIMARY KEY AUTO_INCREMENT,
                    user TEXT       NOT NULL,
                    message TEXT    NOT NULL,
                    timestamp TEXT  NOT NULL
                );
            """.trimIndent()
        ).execute()
    }

}