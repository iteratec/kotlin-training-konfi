package client.model

import kotlin.js.Date

class Conference(
    val id: Long? = null,
    val name: String,
    val maxAttendees: Int = 0,
    val startDate: Date,
    val endDate: Date
)