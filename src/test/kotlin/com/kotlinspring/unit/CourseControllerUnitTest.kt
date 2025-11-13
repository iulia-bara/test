package com.kotlinspring.unit

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlinspring.controller.CourseController
import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.kotlinspring.util.courseDTO
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import kotlin.test.assertEquals
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put


@WebMvcTest(controllers = [CourseController::class])
class CourseControllerUnitTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var courseServiceMock: CourseService

    @Test
    fun addCourse() {

        val courseDTO = courseDTO(null, "Build RestFul APis using SpringBoot and Kotlin",
            "Dilip", 1)
        //val savedCourseDTO = courseDTO(id = 1)   // simulate the saved course returned by service

        val savedCourseDTO = courseDTO.copy(id = 1)

        every { courseServiceMock.addCourse(any()) } returns savedCourseDTO
        //Stubs the behavior of the mocked service (so the controller behaves as expected).

        // when + then
        mockMvc.perform( //.perform() is the mirror of exchange()
            post("/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO))
                    //Converts your Kotlin object to JSON automatically.
                    //Keeps your test synced with your DTO definition.
        )
            //no deserialization ; This uses JSONPath assertions directly on the array response.
            //for when you want to check the response inline — no need to parse JSON:
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(savedCourseDTO.id))
            .andExpect(jsonPath("$.name").value(savedCourseDTO.name))
            .andExpect(jsonPath("$.category").value(savedCourseDTO.category))
            .andExpect(jsonPath("$.instructorId").value(savedCourseDTO.instructorId))

        verify { courseServiceMock.addCourse(any()) }
        //Ensures the controller actually called the service method.
    }

    @Test
    fun addCourse_validation() {
        val invalidCourseDTO = courseDTO(name = "", category = "", instructorId =1)

        // Mock the service (won't be called because validation fails)
        every { courseServiceMock.addCourse(any()) } returns courseDTO(id = 1)

        // Perform request + get response body
        val response: String = mockMvc.perform(
            post("/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourseDTO))
        )
            .andExpect(status().isBadRequest) // Expect 400 due to validation
            .andReturn()                        // return MvcResult
            .response
            .contentAsString                     // response body

        println("Validation response: $response")

        // Assert the response content
        assertEquals(
            "courseDTO.category must not be blank, courseDTO.name must not be blank",
            response
        )

        // Verify service was never called
        verify(exactly = 0) { courseServiceMock.addCourse(any()) } //That assertion only works for validation failures, not runtime exceptions in the service.
    }

    @Test
    fun addCourse_runtimeException() {
        val invalidCourseDTO = CourseDTO(null,"Build RestFul APis using SpringBoot and Kotlin", "Dilip Sundarraj", 1)

        val errorMessage =  "Unexpected error occurred"
        every { courseServiceMock.addCourse(any())} throws RuntimeException(errorMessage)

        // Perform request + get response body
        val response: String = mockMvc.perform(
            post("/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourseDTO))
        )
            .andExpect(status().is5xxServerError) // expect 500
            .andReturn()                        // return MvcResult
            .response
            .contentAsString                     // response body

        println("Validation response: $response")

        // Assert the response content
        assertEquals(
            errorMessage,
            response
        )
    }


    @Test
    fun retrieveAllCourses() {

        every{ courseServiceMock.retrieveAllCourses(any())}.returnsMany( //returnsMany to use in case of collection returns
            listOf(courseDTO(id=1),
                courseDTO(id=2, name="Build Reactive Microservices using Spring WebFlux/SpringBoot"))
        )

        val mvcResult = mockMvc.perform(
            get("/v1/courses")
                //This sends a mock HTTP GET to the controller endpoint, exactly like webTestClient.get().uri("/v1/courses").
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn() // mirror of returnResult()
            //Equivalent to .returnResult() in WebTestClient — capture the raw response.

        // then
        val responseBody = mvcResult.response.contentAsString
        //Equivalent to .responseBody.

        val courseList: List<CourseDTO> = objectMapper.readValue(
            //Deserializes the JSON array into a Kotlin List<CourseDTO> — just like .expectBodyList(CourseDTO::class.java).
            responseBody,
            objectMapper.typeFactory.constructCollectionType(List::class.java, CourseDTO::class.java)
        )

        println("courseDTOs: $courseList")

        assertEquals(2, courseList.size)
        assertEquals(1, courseList[0].id)
        assertEquals("Build Reactive Microservices using Spring WebFlux/SpringBoot", courseList[1].name)

        verify { courseServiceMock.retrieveAllCourses(any()) }
    }

    @Test
    fun updateCourse() {
        // given
        val courseId = 100

        val existingCourse = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin",
            "Development"
        )

        val updatedCourseDTO = CourseDTO(
            null,
            "Build RestFul APis using SpringBoot and Kotlin1",
            "Development"
        )

        every { courseServiceMock.updateCourse(courseId, any()) } returns courseDTO(
            id = 100,
            name = "Build RestFul APis using SpringBoot and Kotlin1"
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/v1/courses/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON)
                //Tells Spring the request body is JSON; required for `@RequestBody` parsing
                //.accept(MediaType.APPLICATION_JSON)`Tells Spring/MockMvc the client expects JSON in the response

                .content(objectMapper.writeValueAsString(updatedCourseDTO))
        )
            .andExpect(status().isOk)
            .andReturn() // equivalent to returnResult()

        // then
        val responseBody = mvcResult.response.contentAsString
        val actualUpdatedCourse = objectMapper.readValue(responseBody, CourseDTO::class.java)

        assertEquals("Build RestFul APis using SpringBoot and Kotlin1", actualUpdatedCourse.name)
        assertEquals(100, actualUpdatedCourse.id)

        verify { courseServiceMock.updateCourse(courseId, any()) }

        @Test
        fun deleteCourse(){

            every{courseServiceMock.deleteCourse(any())} just runs// just runs for no return

            val mvcResult = mockMvc.perform(
                delete("/v1/courses/{courseId}", 123))
                .andExpect(status().isNoContent)

                verify { courseServiceMock.deleteCourse(123) }
        }
    }
}

