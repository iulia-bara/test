package com.kotlinspring.service

import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.entity.Instructor
import com.kotlinspring.InstructorRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService (val instructorRepository: InstructorRepository) {
//    fun createInstructor(instructorDTO: InstructorDTO): InstructorDTO {
//
//        val instructorEntity = instructorDTO.let {
//            Instructor(it.id, it.name)
//        }
//        instructorRepository.save(instructorEntity)
//
//        return instructorEntity.let {
//            InstructorDTO(it.id, it.name)
//        }
//
//    }

    fun addInstructor(instructorDTO: InstructorDTO): InstructorDTO {
        val logger = KotlinLogging.logger {}
        val instructorEntity = instructorDTO.let {
            Instructor(it.id, it.name)
        }
        instructorRepository.save(instructorEntity)

        logger.info("Saved instructor is: $instructorDTO")

        return instructorEntity.let{
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> {
        return instructorRepository.findById(instructorId)
    }
}
