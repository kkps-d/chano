package dev.chano

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ServerInfo(val name: String, val version: String, val message: String)

fun createHttpClient() = HttpClient {
    expectSuccess = true
    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    install(HttpTimeout) {
        requestTimeoutMillis = 10_000
        connectTimeoutMillis = 10_000
    }
}

class ApiClient(private val baseUrl: String, private val client: HttpClient) {
    suspend fun info(): ServerInfo = client.get("${baseUrl.trimEnd('/')}/api/v1/info").body()
}
