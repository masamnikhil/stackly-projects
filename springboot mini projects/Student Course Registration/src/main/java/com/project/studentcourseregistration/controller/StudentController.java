package com.project.studentcourseregistration.controller;

import com.project.studentcourseregistration.dto.StudentRequest;
import com.project.studentcourseregistration.model.Student;
import com.project.studentcourseregistration.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {


    private final StudentService studentService;

    // Register student
    @PostMapping
    public ResponseEntity<Student> registerStudent(
            @Valid @RequestBody StudentRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.registerStudent(dto));
    }

    // View all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {

        return ResponseEntity.ok(
                studentService.getAllStudents()
        );
    }

    // Find by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id) {

        Student student =
                studentService.getStudentById(id);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

    // Delete registration
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {

        boolean deleted =
                studentService.deleteStudent(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return new ResponseEntity<>("Student not found with id: " + id, HttpStatus.NOT_FOUND);
    }

}
