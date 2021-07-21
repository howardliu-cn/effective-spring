package cn.howardliu.effective.spring.springbootjunit5mockio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.effective.spring.springbootjunit5mockio.service.EchoService;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-21
 */
@RestController
@RequestMapping("echo")
public class EchoController {
    @Autowired
    private EchoService echoService;

    @RequestMapping("/")
    public String echo(@RequestParam(value = "name", defaultValue = "123") String name) {
        return echoService.echo(name);
    }
}
