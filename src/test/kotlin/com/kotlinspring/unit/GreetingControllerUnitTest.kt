package com.kotlinspring.unit

import com.kotlinspring.controller.GreetingController
import com.kotlinspring.service.GreetingsService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@WebMvcTest(controllers = [GreetingController::class])
class GreetingControllerUnitTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var greetingServiceMock: GreetingsService

    @Test
    fun retrieveGreeting() { // Keep retrieveGreeting logic the same in test method, but you must use MockMvc, because WebTestClient cannot run in a slice test.
        val name = "Dilip"
        every { greetingServiceMock.retrieveGreeting(any()) } returns "$name, Hello from default profile"

        mockMvc.perform(get("/v1/greetings/$name"))
            .andExpect(status().isOk)
            .andExpect(content().string("$name, Hello from default profile"))
    }
}
