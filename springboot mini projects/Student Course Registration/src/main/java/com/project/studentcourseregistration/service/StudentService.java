package com.project.studentcourseregistration.service;

import com.project.studentcourseregistration.dto.StudentRequest;
import com.project.studentcourseregistration.model.Student;

import java.util.List;

public interface StudentService {

    Student registerStudent(StudentRequest student);
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    boolean deleteStudent(Long id);
}
