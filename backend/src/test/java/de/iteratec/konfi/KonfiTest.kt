package de.iteratec.konfi

import org.hamcrest.Matchers
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import javax.transaction.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class KonfiTest {

    @Autowired
    private val mvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun createAndList() {
        val json = createConferenceJson()
        postJson("/conference", json)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.greaterThan(0)))
        mvc!!.perform(MockMvcRequestBuilders.get("/conference"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.`is`(json.getString("name"))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].maxAttendees", Matchers.`is`(json.getInt("maxAttendees"))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].startDate", Matchers.`is`(json.getString("startDate"))))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].endDate", Matchers.`is`(json.getString("endDate"))))
    }

    @Test
    @Throws(Exception::class)
    fun createInvalid() {
        val json = createConferenceJson()
        postJson("/conference", json.put("name", null)).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("name", "a")).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("name", "a".repeat(60))).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("maxAttendees", null)).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("maxAttendees", -1)).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("startDate", null)).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson(
            "/conference",
            json.put("startDate", "1970-01-03 09:00:00")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
        postJson("/conference", json.put("endDate", null)).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun createColliding() {
        val json = createConferenceJson()
        postJson("/conference", json).andExpect(MockMvcResultMatchers.status().isOk)
        postJson("/conference", json).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun register() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc!!.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isOk)
        mvc.perform(MockMvcRequestBuilders.get("/conference/{id}/attendees", conferenceId))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.`is`(attendeeJson.getString("email"))))
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$[0].firstName",
                    Matchers.`is`(attendeeJson.getString("firstName"))
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$[0].lastName",
                    Matchers.`is`(attendeeJson.getString("lastName"))
                )
            )
    }

    @Test
    @Throws(Exception::class)
    fun registerInvalidAttendee() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson().put("email", "a")
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc!!.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun registerDuplicateEmail() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc!!.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isOk)
        mvc.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun registerMaxAttendeesExceeded() {
        val conferenceId = createConference(createConferenceJson().put("maxAttendees", 1))
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createAttendeeJson().put("email", "test1@konfi.io").toString())
        mvc!!.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isOk)
        val registerRequest2 = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createAttendeeJson().put("email", "test2@konfi.io").toString())
        mvc.perform(registerRequest2).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun deregister() {
        val conferenceId = createConference(createConferenceJson())
        val attendeeJson = createAttendeeJson()
        val registerRequest = MockMvcRequestBuilders.post("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        val deregisterRequest = MockMvcRequestBuilders.delete("/conference/{id}/attendees", conferenceId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(attendeeJson.toString())
        mvc!!.perform(registerRequest).andExpect(MockMvcResultMatchers.status().isOk)
        mvc.perform(deregisterRequest).andExpect(MockMvcResultMatchers.status().isOk)
        mvc.perform(MockMvcRequestBuilders.get("/conference/{id}/attendees", conferenceId))
            .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Any>(0)))
    }

    @Throws(Exception::class)
    private fun createConference(conferenceJson: JSONObject): Long {
        val request = MockMvcRequestBuilders.post("/conference")
            .contentType(MediaType.APPLICATION_JSON)
            .content(conferenceJson.toString())
        val createdConferenceJson = mvc!!.perform(request).andReturn().response.contentAsString
        return JSONObject(createdConferenceJson).getLong("id")
    }

    @Throws(Exception::class)
    private fun postJson(path: String, json: JSONObject): ResultActions {
        return mvc!!.perform(
            MockMvcRequestBuilders.post(path).contentType(MediaType.APPLICATION_JSON).content(json.toString())
        )
    }

    @Throws(Exception::class)
    private fun createConferenceJson(): JSONObject {
        return JSONObject()
            .put("name", "KotlinKonf")
            .put("maxAttendees", 200)
            .put("startDate", "2021-01-03 09:00")
            .put("endDate", "2021-01-05 18:00")
    }

    @Throws(Exception::class)
    private fun createAttendeeJson(): JSONObject {
        return JSONObject()
            .put("firstName", "Max")
            .put("lastName", "Mustermann")
            .put("email", "maxi@konfi.io")
    }

}