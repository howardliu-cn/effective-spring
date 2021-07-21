package cn.howardliu.effective.spring.springbootjunit5mockio.service.impl;

import org.springframework.stereotype.Service;

import cn.howardliu.effective.spring.springbootjunit5mockio.service.EchoService;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-21
 */
@Service
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String foo) {
        return "Hello, " + foo;
    }
}
