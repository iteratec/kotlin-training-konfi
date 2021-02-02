package de.iteratec.konfi;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class KonfiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void createAndList() throws Exception {
        JSONObject json = createConferenceJson();

        postJson("/conference", json)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", greaterThan(0)));

        mvc.perform(get("/conference"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(json.getString("name"))))
                .andExpect(jsonPath("$[0].maxAttendees", is(json.getInt("maxAttendees"))))
                .andExpect(jsonPath("$[0].startDate", is(json.getString("startDate"))))
                .andExpect(jsonPath("$[0].endDate", is(json.getString("endDate"))));
    }

    @Test
    public void createInvalid() throws Exception {
        JSONObject json = createConferenceJson();
        postJson("/conference", json.put("name", null)).andExpect(status().isBadRequest());
        postJson("/conference", json.put("name", "a")).andExpect(status().isBadRequest());
        postJson("/conference", json.put("name", "a".repeat(60))).andExpect(status().isBadRequest());
        postJson("/conference", json.put("maxAttendees", null)).andExpect(status().isBadRequest());
        postJson("/conference", json.put("maxAttendees", -1)).andExpect(status().isBadRequest());
        postJson("/conference", json.put("startDate", null)).andExpect(status().isBadRequest());
        postJson("/conference", json.put("startDate", "1970-01-03T09:00:00")).andExpect(status().isBadRequest());
        postJson("/conference", json.put("endDate", null)).andExpect(status().isBadRequest());
    }

    @Test
    public void createColliding() throws Exception {
        JSONObject json = createConferenceJson();
        postJson("/conference", json).andExpect(status().isOk());
        postJson("/conference", json).andExpect(status().isBadRequest());
    }

    @Test
    public void register() throws Exception {
        Long conferenceId = createConference(createConferenceJson());
        JSONObject attendeeJson = createAttendeeJson();
        MockHttpServletRequestBuilder registerRequest = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendeeJson.toString());

        mvc.perform(registerRequest).andExpect(status().isOk());
        mvc.perform(get("/conference/{id}/attendees", conferenceId))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is(attendeeJson.getString("email"))))
                .andExpect(jsonPath("$[0].firstName", is(attendeeJson.getString("firstName"))))
                .andExpect(jsonPath("$[0].lastName", is(attendeeJson.getString("lastName"))));
    }

    @Test
    public void registerInvalidAttendee() throws Exception {
        Long conferenceId = createConference(createConferenceJson());
        JSONObject attendeeJson = createAttendeeJson().put("email", "a");
        MockHttpServletRequestBuilder registerRequest = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendeeJson.toString());

        mvc.perform(registerRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void registerDuplicateEmail() throws Exception {
        Long conferenceId = createConference(createConferenceJson());
        JSONObject attendeeJson = createAttendeeJson();
        MockHttpServletRequestBuilder registerRequest = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendeeJson.toString());

        mvc.perform(registerRequest).andExpect(status().isOk());
        mvc.perform(registerRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void registerMaxAttendeesExceeded() throws Exception {
        Long conferenceId = createConference(createConferenceJson().put("maxAttendees", 1));
        MockHttpServletRequestBuilder registerRequest = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAttendeeJson().put("email", "test1@konfi.io").toString());
        mvc.perform(registerRequest).andExpect(status().isOk());

        MockHttpServletRequestBuilder registerRequest2 = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAttendeeJson().put("email", "test2@konfi.io").toString());
        mvc.perform(registerRequest2).andExpect(status().isBadRequest());
    }

    @Test
    public void deregister() throws Exception {
        Long conferenceId = createConference(createConferenceJson());
        JSONObject attendeeJson = createAttendeeJson();
        MockHttpServletRequestBuilder registerRequest = post("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendeeJson.toString());
        MockHttpServletRequestBuilder deregisterRequest = delete("/conference/{id}/attendees", conferenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendeeJson.toString());

        mvc.perform(registerRequest).andExpect(status().isOk());
        mvc.perform(deregisterRequest).andExpect(status().isOk());
        mvc.perform(get("/conference/{id}/attendees", conferenceId))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Long createConference(JSONObject conferenceJson) throws Exception {
        MockHttpServletRequestBuilder request = post("/conference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(conferenceJson.toString());

        String createdConferenceJson = mvc.perform(request).andReturn().getResponse().getContentAsString();
        return new JSONObject(createdConferenceJson).getLong("id");
    }

    private ResultActions postJson(String path, JSONObject json) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json.toString()));
    }

    private JSONObject createConferenceJson() throws Exception {
        return new JSONObject()
                .put("name", "KotlinKonf")
                .put("maxAttendees", 200)
                .put("startDate", "2021-01-03T09:00:00")
                .put("endDate", "2021-01-05T18:00:00");
    }

    private JSONObject createAttendeeJson() throws Exception {
        return new JSONObject()
                .put("firstName", "Max")
                .put("lastName", "Mustermann")
                .put("email", "maxi@konfi.io");
    }
}
