package com.mygate.visitor.controller;

import com.mygate.visitor.entity.Student;
import com.mygate.visitor.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Student> students = studentRepository.findAll();
            response.put("success", true);
            response.put("students", students);
            response.put("count", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching all students", e);
            response.put("success", false);
            response.put("message", "Error fetching students: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{regNo}")
    public ResponseEntity<Map<String, Object>> getStudentByRegNo(@PathVariable String regNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Student> student = studentRepository.findByRegNo(regNo);
            if (student.isPresent()) {
                response.put("success", true);
                response.put("student", student.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Student not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error fetching student by regNo: " + regNo, e);
            response.put("success", false);
            response.put("message", "Error fetching student: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<Map<String, Object>> getStudentsByDepartment(@PathVariable String department) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Student> students = studentRepository.findByDepartment(department);
            response.put("success", true);
            response.put("students", students);
            response.put("count", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching students by department: " + department, e);
            response.put("success", false);
            response.put("message", "Error fetching students: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
