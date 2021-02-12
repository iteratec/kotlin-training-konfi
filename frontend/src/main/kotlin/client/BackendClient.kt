package client

import client.model.Attendee
import client.model.Conference
import client.model.Response
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlin.js.Date
import kotlin.js.Json

object BackendClient {

    private const val baseUrl: String = "http://localhost:8080"

    suspend fun getConferences(): Response<List<Conference>> = Response {
        window
            .fetch("$baseUrl/conference").await()
            .json().await()
            .unsafeCast<Array<Json>>()
            .map {
                Conference(
                    id = (it["id"] as Number).toLong(),
                    name = it["name"] as String,
                    maxAttendees = it["maxAttendees"] as Int,
                    startDate = Date(it["startDate"] as String),
                    endDate = Date(it["endDate"] as String)
                )
            }
    }

    suspend fun getAttendees(conferenceId: Long): Response<List<Attendee>> = Response {
        val json = window
            .fetch("$baseUrl/conference/$conferenceId/attendees").await()
            .text().await()
        kotlinx.serialization.json.Json.decodeFromString(json)
    }
}