package de.iteratec.konfi.domain;

import de.iteratec.konfi.domain.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConferenceRepository extends JpaRepository<Conference, Long> {

    List<Conference> findByName(String name);
}
