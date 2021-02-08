package components

import client.BackendClient
import client.model.Attendee
import client.model.Conference
import client.model.Response
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.*
import react.dom.div
import react.dom.h1

external interface AppState : RState {
    var conferences: List<Conference>
    var conferencesError: Boolean
    var selectedConference: Conference?
    var selectedConferenceAttendees: List<Attendee>
    var selectedConferenceAttendeesError: Boolean
}

class App : RComponent<RProps, AppState>() {
    override fun RBuilder.render() {
        div("container p-4") {
            div("row") {
                div("col-md-12") {
                    h1 { +"Konfi" }
                }
            }
            div("row") {
                div("col-md-4") {
                    conferenceListComp {
                        conferences = state.conferences
                        hasError = state.conferencesError
                        onSelect = this@App::selectConference
                    }
                }
                div("col-md-8") {
                    conferenceViewComp {
                        conference = state.selectedConference
                        attendees = state.selectedConferenceAttendees
                        attendeesError = state.selectedConferenceAttendeesError
                    }
                }
            }
        }
    }

    private fun selectConference(conference: Conference?) {
        setState {
            selectedConference = conference
        }

        val conferenceId = conference?.id
        if (conferenceId != null) {
            MainScope().launch {
                when (val response = BackendClient.getAttendees(conferenceId)) {
                    is Response.Success -> setState {
                        selectedConferenceAttendees = response.value
                        selectedConferenceAttendeesError = false
                    }
                    is Response.Error -> setState {
                        selectedConferenceAttendees = emptyList()
                        selectedConferenceAttendeesError = true
                    }
                }
            }
        }
    }

    override fun AppState.init() {
        conferences = listOf()
        conferencesError = false
        selectedConference = null
        selectedConferenceAttendees = emptyList()
        selectedConferenceAttendeesError = false

        MainScope().launch {
            when (val response = BackendClient.getConferences()) {
                is Response.Success -> setState {
                    conferencesError = false
                    conferences = response.value
                }
                is Response.Error -> setState {
                    conferencesError = true
                    console.log(response.error)
                }
            }
        }
    }
}
