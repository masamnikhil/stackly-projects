package com.project.studentcourseregistration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    private Long studentId;

    private String studentName;

    private String courseName;

    private String email;

    private int age;
}
