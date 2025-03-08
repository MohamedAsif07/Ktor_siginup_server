
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty) // Netty engine

    // Authentication (JWT)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Serialization
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)

    // Logging
    implementation(libs.logback.classic)

    // Configuration Handling (YAML Support)
    implementation(libs.ktor.server.config.yaml)

    // Database (PostgreSQL + HikariCP + Exposed ORM)
    implementation(libs.postgresql)
    implementation(libs.h2) // Only if you're using an in-memory DB for testing
    implementation("com.zaxxer:HikariCP:5.0.1") // HikariCP for database connection pooling
    implementation("org.jetbrains.exposed:exposed-core:0.43.0") // Exposed ORM Core
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0") // DAO support
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0") // JDBC support

    // BCrypt for Password Hashing
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3") // Re
    implementation("io.jsonwebtoken:jjwt:0.12.5")

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
