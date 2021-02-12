package de.iteratec.konfi.domain

import de.iteratec.konfi.domain.model.Conference
import org.springframework.data.jpa.repository.JpaRepository

interface ConferenceRepository : JpaRepository<Conference, Long> {
    fun findByName(name: String): List<Conference>
}
