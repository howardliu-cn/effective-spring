package cn.howardliu.effective.spring.springaspect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.effective.spring.springaspect.aspect.MethodSignature;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2021-08-10
 */
@RestController
public class HomeController {
    @GetMapping("hello")
    @MethodSignature
    public String helloWorld() {
        return "Hello, World!";
    }
}
