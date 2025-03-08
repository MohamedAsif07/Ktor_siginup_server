package com.example

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class UserCredentials(val username: String, val password: String)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 255).uniqueIndex()
    val passwordHash = text("password_hash")
    override val primaryKey = PrimaryKey(id)
}

class UserRepository {
    fun registerUser(username: String, password: String): Boolean {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        return transaction {
            try {
                Users.insert {
                    it[Users.username] = username
                    it[Users.passwordHash] = hashedPassword
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun findUser(username: String): Pair<Int, String>? {
        return transaction {
            Users.select { Users.username eq username }
                .map { it[Users.id] to it[Users.passwordHash] }
                .singleOrNull()
        }
    }
}

fun Application.configureRouting() {
    routing {
        route("/auth") {
            val userRepo = UserRepository()

            post("/register") {
                val credentials = call.receive<UserCredentials>()
                val success = userRepo.registerUser(credentials.username, credentials.password)

                if (success) {
                    call.respond(mapOf("message" to "User registered successfully"))
                } else {
                    call.respond(mapOf("error" to "User already exists"))
                }
            }

            post("/login") {
                val credentials = call.receive<UserCredentials>()
                val user = userRepo.findUser(credentials.username)

                if (user != null && BCrypt.checkpw(credentials.password, user.second)) {
                    val token = JwtConfig.generateToken(credentials.username)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(mapOf("error" to "Invalid credentials"))
                }
            }

            authenticate("auth-jwt") {
                get("/protected") {
                    call.respond(mapOf("message" to "You are authenticated"))
                }
            }
        }
    }
}
