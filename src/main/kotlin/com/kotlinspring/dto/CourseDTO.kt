package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CourseDTO(
    val id: Int?,
    @get: NotBlank(message = "courseDTO.name must not be blank")
    val name: String,
    @get: NotBlank(message = "courseDTO.category must not be blank")
    val category: String,
    @get:NotNull(message = "courseDTO.instructorId must not be null")
    val instructorId: Int? = null,

    //Bean Validation: to ensure that anytime a new course instance is added, to make sure that certain rules are met
    //ex: to make sure that name and category is present
    //implementation("org.springframework.boot:spring-boot-starter-validation") -> need this in the build file (annotations)
)