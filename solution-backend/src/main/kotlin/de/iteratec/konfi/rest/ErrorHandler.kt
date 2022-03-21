package de.iteratec.konfi.rest

import org.springframework.web.bind.annotation.ControllerAdvice
import de.iteratec.konfi.domain.model.ConferenceCollisionException
import de.iteratec.konfi.domain.model.RegisterAttendeeException
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.Exception

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(value = [ConferenceCollisionException::class, RegisterAttendeeException::class])
    @ResponseBody
    fun handle(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}