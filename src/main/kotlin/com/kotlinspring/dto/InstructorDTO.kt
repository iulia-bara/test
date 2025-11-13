package com.kotlinspring.dto

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank


data class InstructorDTO(
    val id: Int?,
    @get: NotBlank(message = "instructorDTO.name must not be blank")
    var name: String
)
