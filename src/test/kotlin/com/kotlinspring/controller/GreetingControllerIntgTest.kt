package com.kotlinspring.controller

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import com.kotlinspring.CourseCatalogServiceApplication
import com.kotlinspring.CourseRepository


@SpringBootTest(
    classes = [CourseCatalogServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
) //dynamic port
@ActiveProfiles("test")
@AutoConfigureWebTestClient //scan de controller and automatically make the endpoints available to the webTestClient instance
//it will automatically detect the port on which the service is running and is going to configure it

class GreetingControllerIntgTest(@Autowired private val courseRepository: CourseRepository) {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun retrieveGreeting(){
        val name = "Dilip"

        val result = webTestClient.get()
            .uri("/v1/greetings/{name}", name)
            .exchange() //fundamentally will make the call to the endpoint
            .expectStatus().is2xxSuccessful
            .expectBody(String::class.java) //type
            .returnResult()

        Assertions.assertEquals("$name, Hello from default profile", result.responseBody) // what we are expecting
    }



}