package de.iteratec.konfi

import org.hamcrest.Matchers.*
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.transaction.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class KonfiTest @Autowired constructor(private val mvc: MockMvc) {

    @Test
    fun createAndList() {
        val json = createConferenceJson()
        postJson("/conference", json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", greaterThan(0)))
        mvc.perform(MockMvcRequestBuilders.get("/conference"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].name", `is`(json.getString("name"))))
            .andExpect(jsonPath("$[0].maxAttendees", `is`(json.getInt("maxAttendees"))))
            .andExpect(jsonPath("$[0].startDate", `is`(json.getString("startDate"))))
            .andExpect(jsonPath("$[0].endDate", `is`(json.getString("endDate"))))
    }

    @Test
    fun createInvalid() {
        val json = createConferenceJson()
        postJson("/conference", json.put("name", null)).andExpect(status().isBadRequest)
        postJson("/conference", json.put("name", "a")).andExpect(status().isBadRequest)
        postJson("/conference", json.put("name", "a".repeat(60))).andExpect(status().isBadRequest)
        postJson("/conference", json.put("maxAttendees", null)).andExpect(status().isBadRequest)
        postJson("/conference", json.put("maxAttendees", -1)).andExpect(status().isBadRequest)
        postJson("/conference", json.put("startDate", null)).andExpect(status().isBadRequest)
        postJson("/conference", json.put("startDate", "1970-01-03 09:00:00")).andExpect(status().isBadRequest)
        postJson("/conference", json.put("endDate", null)).andExpect(status().isBadRequest)
    }

    @Test
    fun createColliding() {
        val json = createConferenceJson()
        postJson("/conference", json).andExpect(status().isOk)
        postJson("/conference", json).andExpect(status().isBadRequest)
    }

    @Test
    fun register() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc.perform(registerRequest).andExpect(status().isOk)
        mvc.perform(MockMvcRequestBuilders.get("/conference/{id}/attendees", conferenceId))
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].email", `is`(attendeeJson.getString("email"))))
            .andExpect(jsonPath("$[0].firstName", `is`(attendeeJson.getString("firstName"))))
            .andExpect(jsonPath("$[0].lastName", `is`(attendeeJson.getString("lastName"))))
    }

    @Test
    fun registerInvalidAttendee() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson().put("email", "a")
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc.perform(registerRequest).andExpect(status().isBadRequest)
    }

    @Test
    fun registerDuplicateEmail() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc.perform(registerRequest).andExpect(status().isOk)
        mvc.perform(registerRequest).andExpect(status().isBadRequest)
    }

    @Test
    fun registerMaxAttendeesExceeded() {
        val conferenceId = createConference(createConferenceJson().put("maxAttendees", 1))
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createAttendeeJson().put("email", "test1@konfi.io").toString())
        mvc.perform(registerRequest).andExpect(status().isOk)
        val registerRequest2 = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createAttendeeJson().put("email", "test2@konfi.io").toString())
        mvc.perform(registerRequest2).andExpect(status().isBadRequest)
    }

    @Test
    fun deregister() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        val deregisterRequest = MockMvcRequestBuilders.delete("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc.perform(registerRequest).andExpect(status().isOk)
        mvc.perform(deregisterRequest).andExpect(status().isOk)
        mvc.perform(MockMvcRequestBuilders.get("/conference/{id}/attendees", conferenceId))
            .andExpect(jsonPath("$", hasSize<Any>(0)))
    }

    private fun createConference(conferenceJson: JSONObject): Long {
        val request = MockMvcRequestBuilders.post("/conference")
            .contentType(MediaType.APPLICATION_JSON)
            .content(conferenceJson.toString())
        val createdConferenceJson = mvc.perform(request).andReturn().response.contentAsString
        return JSONObject(createdConferenceJson).getLong("id")
    }

    private fun postJson(path: String, json: JSONObject): ResultActions {
        return mvc.perform(
            MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON).content(json.toString())
        )
    }

    private fun createConferenceJson(): JSONObject {
        return JSONObject()
            .put("name", "KotlinKonf")
            .put("maxAttendees", 200)
            .put("startDate", "2021-01-03 09:00")
            .put("endDate", "2021-01-05 18:00")
    }

    private fun createAttendeeJson(): JSONObject {
        return JSONObject()
            .put("firstName", "Max")
            .put("lastName", "Mustermann")
            .put("email", "maxi@konfi.io")
    }

}
