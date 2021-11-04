package cn.howardliu.demo.spring.seh.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.demo.spring.seh.pojo.User;

/**
 *
 created at 2020/8/12
 *
 * @author www.howardliu.cn
 * @since 1.0.0
 */
@RestController
public class HelloWorldController {
    @RequestMapping("hello2")
    public String hello2() {
        return "Hello, World!";
    }

    @RequestMapping("user1")
    public User user1() {
        User u = new User();
        u.setUid(System.currentTimeMillis() + "");
        u.setName("测试1");
        return u;
    }
}
