package de.iteratec.konfi

import org.hamcrest.Matchers.*
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class KonfiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `created conference appears on a conference list`() {
        val json = createConferenceJson()

        post("/conference")
            .jsonContent(json)
            .execute()
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", greaterThan(0)))

        get("/conference")
            .execute()
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Int>(1)))
            .andExpect(jsonPath("$[0].name", equalTo(json.getString("name"))))
            .andExpect(jsonPath("$[0].maxAttendees", equalTo(json.getInt("maxAttendees"))))
            .andExpect(jsonPath("$[0].startDate", equalTo(json.getString("startDate"))))
            .andExpect(jsonPath("$[0].endDate", equalTo(json.getString("endDate"))))
    }

    @Test
    fun `conferences with invalid attributes are refused`() {
        fun assertFails(json: JSONObject) {
            post("/conference").jsonContent(json).execute().andExpect(status().isBadRequest)
        }

        val json = createConferenceJson()
        assertFails(json.put("name", null))
        assertFails(json.put("name", "a"))
        assertFails(json.put("name", "a".repeat(60)))
        assertFails(json.put("maxAttendees", null))
        assertFails(json.put("maxAttendees", -1))
        assertFails(json.put("startDate", null))
        assertFails(json.put("startDate", "1970-01-03T09:00:00"))
        assertFails(json.put("endDate", null))
    }

    @Test
    fun `simultaneous conferences with the same name are not allowed`() {
        val json = createConferenceJson()
        post("/conference").jsonContent(json).execute().andExpect(status().isOk)
        post("/conference").jsonContent(json).execute().andExpect(status().isBadRequest)
    }

    @Test
    fun `registered attendee appears on the attendee list`() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()

        post("/conference/{id}/attendees", conferenceId)
            .jsonContent(attendeeJson)
            .execute()
            .andExpect(status().isOk)

        get("/conference/{id}/attendees", conferenceId)
            .execute()
            .andExpect(jsonPath("$", hasSize<Int>(1)))
            .andExpect(jsonPath("$[0].email", equalTo(attendeeJson.getString("email"))))
            .andExpect(jsonPath("$[0].firstName", equalTo(attendeeJson.getString("firstName"))))
            .andExpect(jsonPath("$[0].lastName", equalTo(attendeeJson.getString("lastName"))))
    }

    @Test
    fun `attendees with invalid attributes are refused`() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson().put("email", "a")

        post("/conference/{id}/attendees", conferenceId)
            .jsonContent(attendeeJson)
            .execute()
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `attendee may register for a conference only once`() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()

        val registerRequest = post("/conference/{id}/attendees", conferenceId).jsonContent(attendeeJson)
        registerRequest.execute().andExpect(status().isOk)
        registerRequest.execute().andExpect(status().isBadRequest)
    }

    @Test
    fun `maximal attendee limit cannot be exceeded`() {
        val conferenceId = createConference(createConferenceJson().put("maxAttendees", 1))

        post("/conference/{id}/attendees", conferenceId)
            .jsonContent(createAttendeeJson().put("email", "test1@konfi.io"))
            .execute()
            .andExpect(status().isOk)

        post("/conference/{id}/attendees", conferenceId)
            .jsonContent(createAttendeeJson().put("email", "test2@konfi.io"))
            .execute()
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deregistered attendee disappears from the attendee list`() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()

        post("/conference/{id}/attendees", conferenceId)
            .jsonContent(attendeeJson)
            .execute()
            .andExpect(status().isOk)

        delete("/conference/{id}/attendees", conferenceId)
            .jsonContent(attendeeJson)
            .execute()
            .andExpect(status().isOk)

        get("/conference/{id}/attendees", conferenceId)
            .execute()
            .andExpect(jsonPath("$", hasSize<Int>(0)))
    }

    private fun createConference(conferenceJson: JSONObject): Long {
        return post("/conference")
            .jsonContent(conferenceJson)
            .execute()
            .andReturn().response.contentAsString
            .let { JSONObject(it).getLong("id") }
    }

    private fun MockHttpServletRequestBuilder.jsonContent(json: JSONObject) = this
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.toString())

    private fun MockHttpServletRequestBuilder.execute() = mvc.perform(this)

    private fun createConferenceJson() = JSONObject().apply {
        put("name", "KotlinKonf")
        put("maxAttendees", 200)
        put("startDate", "2021-01-03T09:00:00")
        put("endDate", "2021-01-05T18:00:00")
    }

    private fun createAttendeeJson() = JSONObject().apply {
        put("firstName", "Max")
        put("lastName", "Mustermann")
        put("email", "maxi@konfi.io")
    }
}