package com.kotlinspring.exceptionhandler

import com.kotlinspring.exception.InstructorNotValidException
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component //to be scanned as an Bean
@ControllerAdvice //acting as a proxi - to track any kind of expection that it's thrown by code logic that is part of the controller
//Spring annotation that allows you to write global code that applies to multiple controllers

class GlobalErrorHandler : ResponseEntityExceptionHandler() { //ResponseEntityExceptionHandler : we can implement all the custom expection handling

    private val logger = KotlinLogging.logger {}

    override fun handleMethodArgumentNotValid(
        //“If a MethodArgumentNotValidException happens, use my custom logic instead of the default one.”
        ex: MethodArgumentNotValidException, //Contains all validation error details (field names, messages, rejected values, etc.)
        headers: HttpHeaders, //HTTP headers of the original request (rarely used here)
        status: HttpStatusCode, //The default HTTP status (e.g. 400)
        request: WebRequest //Represents the original request (for extra context if needed)
    ): ResponseEntity<in Any>? {

        logger.error("MethodArgumentNotValidException observed:${ex.message}", ex)

        val errors = ex.bindingResult.allErrors
            .map { error -> error.defaultMessage!! } // map the "courseDTO.name must not be blank"
            .sorted() // to receive the same response if we will receive multiple errors

        logger.info("errors: $errors")

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errors.joinToString(", "){it})

    }

    @ExceptionHandler(InstructorNotValidException::class)
    fun handleInstructorNotValidExceptions(ex: InstructorNotValidException, request: WebRequest): ResponseEntity<Any?> {
        logger.error("Exception observed: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<Any?> {
        logger.error("Exception observed: ${ex.message}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.message)
    }


}