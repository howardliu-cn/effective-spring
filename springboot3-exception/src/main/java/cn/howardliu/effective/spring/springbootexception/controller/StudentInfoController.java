package cn.howardliu.effective.spring.springbootexception.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.effective.spring.springbootexception.domain.Student;
import cn.howardliu.effective.spring.springbootexception.domain.StudentNoGetter;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2023-03-14
 */
@RestController
@RequestMapping("/api")
public class StudentInfoController {

    @GetMapping("/got-with-exception/{id}")
    public StudentNoGetter gotException(@PathVariable("id") String id) {
        return new StudentNoGetter(id, "Howard", "Liu", "A");
    }

    @GetMapping(value = "/got-with-exception2/{id}")
    public ResponseEntity<StudentNoGetter> gotException2(@PathVariable("id") String id) {
        return ResponseEntity.ok(new StudentNoGetter(id, "Howard", "Liu", "A"));
    }

    @GetMapping("/got/{id}")
    public Student got(@PathVariable("id") String id) {
        return new Student(id, "Howard", "Liu", "A");
    }
}
