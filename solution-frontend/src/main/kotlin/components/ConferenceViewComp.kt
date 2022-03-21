package components

import client.model.Attendee
import client.model.Conference
import react.*
import react.dom.div
import react.dom.h4
import react.dom.h5
import react.dom.span
import kotlin.js.Date

external interface ConferenceViewProps : RProps {
    var conference: Conference?
    var attendees: List<Attendee>
    var attendeesError: Boolean
}

fun RBuilder.conferenceViewComp(handler: ConferenceViewProps.() -> Unit): ReactElement {
    return child(ConferenceViewComp::class) {
        attrs.conference = null
        attrs(handler)
    }
}

class ConferenceViewComp(props: ConferenceViewProps) : RComponent<ConferenceViewProps, RState>(props) {

    override fun RBuilder.render() {
        div("card p-4 shadow-sm") {
            when (props.conference) {
                null -> renderEmpty()
                else -> renderConference()
            }
        }
    }

    private fun RBuilder.renderEmpty() {
        div("text-center text-muted") {
            span { +"Please select a conference from the list on the left" }
        }
    }

    private fun RBuilder.renderConference() {
        renderConferenceDetails()
        renderAttendees()
    }

    private fun RBuilder.renderConferenceDetails() {
        with(props.conference!!) {
            div {
                h4 { +name }
                div("row") {
                    div("col-md-4") { +"Conference start" }
                    div("col-md-8") { +(startDate.toLocaleDateTimeString()) }
                }
                div("row") {
                    div("col-md-4") { +"Conference end" }
                    div("col-md-8") { +(endDate.toLocaleDateTimeString()) }
                }
                div("row") {
                    div("col-md-4") { +"Attendee limit" }
                    div("col-md-8") { +(maxAttendees.toString()) }
                }
            }
        }
    }

    private fun RBuilder.renderAttendees() {
        div {
            h5("mt-3") { +"Attendee list" }
            when {
                props.attendeesError -> div("text-danger") { +"Error while fetching attendee list." }
                props.attendees.isEmpty() -> div("text-muted") { +"No attendees registered yet." }
                else -> props.attendees.forEach {
                    div("row") {
                        div("col") { +(it.email) }
                        div("col") { +(it.firstName) }
                        div("col") { +(it.lastName) }
                    }
                }
            }
        }
    }

    private fun Date.toLocaleDateTimeString(): String = "${toLocaleDateString()} ${toLocaleTimeString()}"
}