package dev.chano

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ApiClientTest {
    @Test
    fun decodesBackendContractAndNormalizesTrailingSlash() = runTest {
        val client = HttpClient(MockEngine { request ->
            assertEquals("http://localhost:3000/api/v1/info", request.url.toString())
            respond(
                """{"name":"Chano","version":"0.1.0","message":"Connected"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }) { install(ContentNegotiation) { json() } }
        try {
            assertEquals(ServerInfo("Chano", "0.1.0", "Connected"), ApiClient("http://localhost:3000/", client).info())
        } finally { client.close() }
    }

    @Test
    fun serverErrorsAreNotReportedAsAConnection() = runTest {
        val client = HttpClient(MockEngine { respond("Unavailable", HttpStatusCode.ServiceUnavailable) }) {
            expectSuccess = true
        }
        try {
            assertFails { ApiClient("http://localhost:3000", client).info() }
        } finally { client.close() }
    }
}
