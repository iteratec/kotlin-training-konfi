package components

import client.model.Conference
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

external interface ConferenceListCompProps : RProps {
    var conferences: List<Conference>
    var hasError: Boolean
    var onSelect: (Conference?) -> Unit
}

fun RBuilder.conferenceListComp(handler: ConferenceListCompProps.() -> Unit): ReactElement {
    return child(ConferenceListComp::class) {
        attrs.conferences = listOf()
        attrs.hasError = false
        attrs(handler)
    }
}

class ConferenceListComp : RComponent<ConferenceListCompProps, RState>() {

    override fun RBuilder.render() {
        div("conference-list card shadow-sm") {
            h5("text-center my-3") { +"Current conferences" }
            hr("m-0") {}
            when {
                props.hasError -> renderError()
                props.conferences.isEmpty() -> renderEmptyList()
                else -> renderList()
            }
        }
    }

    private fun RBuilder.renderEmptyList() {
        div(classes = "text-center mt-3") {
            p { +"No conferences found." }
        }
    }

    private fun RBuilder.renderList() {
        props.conferences.forEach { conference ->
            div("pl-3 py-2 conference-list__entry") {
                attrs {
                    onClickFunction = { props.onSelect(conference) }
                }
                +conference.name
            }
        }
    }

    private fun RBuilder.renderError() {
        div("text-center text-danger mt-3") {
            p { +"Conferences could not be loaded :(" }
        }
    }
}