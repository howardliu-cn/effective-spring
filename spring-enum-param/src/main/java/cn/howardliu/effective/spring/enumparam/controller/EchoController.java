package cn.howardliu.effective.spring.enumparam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.effective.spring.enumparam.enums.GenderCodeEnum;
import cn.howardliu.effective.spring.enumparam.enums.GenderIdCodeEnum;
import cn.howardliu.effective.spring.enumparam.enums.GenderIdEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-20
 */
@RestController
@RequestMapping("echo")
public class EchoController {
    @GetMapping("gender-id-code")
    public String genderIdCode(@RequestParam("gender") GenderIdCodeEnum gender) {
        return gender.name();
    }

    @GetMapping("gender-code")
    public String genderCode(@RequestParam("gender") GenderCodeEnum gender) {
        return gender.name();
    }

    @GetMapping("gender-id")
    public String genderId(@RequestParam("gender") GenderIdEnum gender) {
        return gender.name();
    }
}
