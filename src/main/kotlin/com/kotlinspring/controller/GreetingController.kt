package com.kotlinspring.controller
import mu.KotlinLogging

import com.kotlinspring.service.GreetingsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController // tells Spring: "This class handles HTTP requests."
//Automatically converts your method’s return value into a web response (usually JSON or text).

@RequestMapping("/v1/greetings") //Sets a base path for all methods in this controller.

class GreetingController(val greetingsService: GreetingsService) {
    private val logger = KotlinLogging.logger {} // creates a class-specific logger

    @GetMapping("/{name}") // tells Spring: "When someone visits the URL /hello with a GET request, call this method."
    fun retrieveGreeting(@PathVariable("name") name:String): String {
        //@PathVariable: Used to grab part of the URL and pass it into your method.
        //The {name} part in the URL becomes a Java variable.
        //Extracts a part of the URL (like Alice) and gives it to your method.

        //return "Hello $name"
        logger.info("Name is $name") // logs the name at INFO level
        return greetingsService.retrieveGreeting(name)
    }
}