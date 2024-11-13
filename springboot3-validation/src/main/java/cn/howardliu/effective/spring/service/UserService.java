package cn.howardliu.effective.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cn.howardliu.effective.spring.entity.User;
import jakarta.validation.Valid;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2024-11-13
 */
@Validated
@Service
public class UserService {

    public void handleUser(@Valid User user) {
        System.out.println("Got validated user " + user);
    }
}
