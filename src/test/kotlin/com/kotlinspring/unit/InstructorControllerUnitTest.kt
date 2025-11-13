package com.kotlinspring.unit

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlinspring.controller.InstructorController
import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.service.InstructorService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import com.kotlinspring.util.instructorDTO
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.String
import kotlin.test.assertEquals


@WebMvcTest(controllers = [InstructorController::class])
class InstructorControllerUnitTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var instructorServiceMock: InstructorService

    @Test
    fun addInstructor() {
        val instructorDTO = instructorDTO()
        val savedInstructorDTO = instructorDTO(1)

        every { instructorServiceMock.addInstructor(any())} returns savedInstructorDTO

        //when+then
        val response = mockMvc.perform(
            post("/v1/instructors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(instructorDTO))
        )
            .andExpect  (status().isCreated)
            .andReturn()
            .response
            .contentAsString

        println("Validation response: $response")

//        assertEquals(
//            "instructorDTO.name must not be blank",
//            response
//        )

        val responseBody = objectMapper.readValue(response, InstructorDTO::class.java)
        assertNotNull(responseBody.id)


        // Verify service was never called
        verify(exactly = 1) { instructorServiceMock.addInstructor(any()) }

    }

    @Test
    fun addInstructor_Validation(){
        val invalidInstructorDTO = InstructorDTO(null,"")

        every { instructorServiceMock.addInstructor(any())} returns invalidInstructorDTO

        val response: String = mockMvc.perform(
            post("/v1/instructors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInstructorDTO))
        )
            .andExpect(status().isBadRequest) // Expect 400 due to validation
            .andReturn()                        // return MvcResult
            .response
            .contentAsString


        // Assert the response content
        assertEquals(
            "instructorDTO.name must not be blank",
            response
        )

        verify(exactly = 0) { instructorServiceMock.addInstructor(any()) }
    }
}