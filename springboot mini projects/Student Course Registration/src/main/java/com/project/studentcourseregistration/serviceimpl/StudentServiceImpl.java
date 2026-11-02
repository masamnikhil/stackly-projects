package com.project.studentcourseregistration.serviceimpl;

import com.project.studentcourseregistration.dto.StudentRequest;
import com.project.studentcourseregistration.exception.StudentNotFoundException;
import com.project.studentcourseregistration.model.Student;
import com.project.studentcourseregistration.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentServiceImpl implements StudentService {

    private final Map<Long, Student> students =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    // Register student
    public Student registerStudent(StudentRequest studentRequest) {

        Long id = idGenerator.getAndIncrement();

        Student student = Student.builder().studentId(id).studentName(studentRequest.getStudentName())
                        .courseName(studentRequest.getCourseName()).email(studentRequest.getEmail())
                        .age(studentRequest.getAge()).build();

        students.put(id, student);

        return student;
    }

    // View all students
    public List<Student> getAllStudents() {

        List<Student> studentList = new ArrayList<>(students.values());

        if(studentList.isEmpty()) {
            throw new StudentNotFoundException("Students not found");
        }

        return studentList;
    }

    // Find student by ID
    public Student getStudentById(Long id) {

        if(!students.containsKey(id))
            throw new StudentNotFoundException("Student not found with id: " + id);

        return students.get(id);
    }

    // Delete student
    public boolean deleteStudent(Long id) {

        return students.remove(id) != null;
    }
}
