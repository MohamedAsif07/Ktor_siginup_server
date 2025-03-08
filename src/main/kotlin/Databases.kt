package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/demo"
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "Asif@2005."
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }
}

fun Application.configureDatabases() {
    DatabaseFactory.init()
}
