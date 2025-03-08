package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

object JwtConfig {
    private const val secretKey = "your_super_secret_key_which_should_be_long" // Must be 32+ characters
    private const val issuer = "auth-server"
    private const val audience = "ktor-audience"
    private const val validityInMs = 36_000_00 * 10 // 10 hours

    private val algorithm = Algorithm.HMAC256(secretKey)

    fun generateToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withIssuer(issuer)
            .withAudience(audience)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
}

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor application"
            verifier(JwtConfig.getVerifier()) // FIX: Use Auth0 JWT Verifier
            validate { credential ->
                if (credential.payload.subject != null) {
                    UserIdPrincipal(credential.payload.subject)
                } else null
            }
        }
    }
}
