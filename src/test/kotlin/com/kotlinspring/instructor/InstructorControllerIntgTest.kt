package com.kotlinspring.instructor

import com.kotlinspring.CourseCatalogServiceApplication
import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.InstructorRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import com.kotlinspring.util.instructorEntityList
import kotlin.test.Test

@SpringBootTest(
    classes = [CourseCatalogServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
) //dynamic port
@ActiveProfiles("test")
@AutoConfigureWebTestClient //scan de controller and automatically make the endpoints available to the webTestClient instance
//it will automatically detect the port on which the service is running and is going to configure it


class InstructorControllerIntgTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    @BeforeEach
    fun setUp() {
        instructorRepository.deleteAll()
        val instructors = instructorEntityList()
        instructorRepository.saveAll(instructors)
    }

    @Test
    fun addInstructor() {
        val instructorDTO = InstructorDTO(null, "Dilip")

        //when
        val savedInstructorDTO = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        //then
        Assertions.assertTrue(savedInstructorDTO!!.id != null)

    }

}