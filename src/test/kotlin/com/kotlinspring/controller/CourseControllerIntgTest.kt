package com.kotlinspring.controller

import com.kotlinspring.CourseCatalogServiceApplication
import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.CourseRepository
import com.kotlinspring.InstructorRepository
import com.kotlinspring.util.PostgresSQLContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import kotlin.test.assertEquals


@SpringBootTest(
    classes = [CourseCatalogServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
) //dynamic port
@ActiveProfiles("test")
@AutoConfigureWebTestClient //scan de controller and automatically make the endpoints available to the webTestClient instance
//it will automatically detect the port on which the service is running and is going to configure it
@Testcontainers

class CourseControllerIntgTest: PostgresSQLContainerInitializer() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository
//
//    companion object { //run test without docker running
//
//        @Container
//        val postgresDB = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:13-alpine")).apply {
//            withDatabaseName("testdb")
//            withUsername("postgres")
//            withPassword("secret")
//        }
//
//        @JvmStatic
//        @DynamicPropertySource //automatically overwrites the default values
//        fun properties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.datasource.url", postgresDB::getJdbcUrl)
//            registry.add("spring.datasource.username", postgresDB::getUsername)
//            registry.add("spring.datasource.password", postgresDB::getPassword)
//        }
//    }

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()

        val instructor = instructorEntity()
        instructorRepository.save(instructor)

        val courses = courseEntityList(instructor)
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {

        val instructor = instructorRepository.findAll().first() //will return the first element

        val courseDTO = CourseDTO(null, "Build Restful APIs using SpringBoot and Kotlin",
            "Dilip Sundarraj", instructor.id)

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange() //make the call to the endpoint; this actually sends the HTTP request
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue(savedCourseDTO!!.id != null)
    }

    @Test
    fun retrieveAllCourses() {

        val courseDTOs =  webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("courseDTOs:  $courseDTOs")
        assertEquals(3, courseDTOs!!.size)
    }

    @Test
    fun retrieveAllCourses_Byname() {

        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name","SpringBoot")
            .toUriString() // uri as a string

        val courseDTOs =  webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("courseDTOs:  $courseDTOs")
        assertEquals(2, courseDTOs!!.size)
    }


    @Test
    fun updateCourse(){

        val instructor = instructorRepository.findAll().first() //will return the first element

        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development", instructor)
        courseRepository.save(course)
        //courseId
        //Updated CourseDTO
        val updatedCourseDTO =  CourseDTO(
            null,
            "Build RestFul APis using SpringBoot and Kotlin1", "Development", course.instructor!!.id)

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange() //make the call to the endpoint
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Build RestFul APis using SpringBoot and Kotlin1", updatedCourse!!.name)
    }

    @Test
    fun deleteCourse(){

        val instructor = instructorRepository.findAll().first() //will return the first element

        //existing course
        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development",instructor)
        courseRepository.save(course)
        //courseId
        //Updated CourseDTO

        val updatedCourse = webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange() //make the call to the endpoint
            .expectStatus().isNoContent

    }
}