package de.iteratec.konfi.rest;

import de.iteratec.konfi.domain.model.ConferenceCollisionException;
import de.iteratec.konfi.domain.model.RegisterAttendeeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(value = {ConferenceCollisionException.class, RegisterAttendeeException.class})
    @ResponseBody
    public ResponseEntity<String> handle(Exception ex) {
        LOG.error("Error while handling request", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
