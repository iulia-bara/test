package com.kotlinspring


import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> { //CrudRepository: you get the implementation for available save(), findAll() etc.

    fun findByNameContaining(courseName: String) : List<Course>

    @Query(value = "SELECT * FROM COURSES where name like %?1% ", nativeQuery = true)
    // ?1 → refers to the first method parameter
    fun findCoursesbyName(courseName : String) : List<Course>

}