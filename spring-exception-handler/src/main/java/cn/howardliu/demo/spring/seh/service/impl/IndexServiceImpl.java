package cn.howardliu.demo.spring.seh.service.impl;

import cn.howardliu.demo.spring.seh.service.IndexService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <br>created at 2020/8/24
 *
 * @author liuxh
 * @since 1.0.0
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Value("${spring.application.name:${spring.application.nickname:World}}")
    private String name;
    @Value("${spring.application.firstName:Howard}")
    private String firstName;
    @Value("${spring.application.secondName:Liu}")
    private String secondName;
    @Value("${spring.application.thirdName:haha}")
    private String thirdName;

    @Override
    public String hello() {
        return "Hello, " + name + "!" + '\n' + "firstName: " + firstName + ' ' + "secondName: " + secondName + ' ' + "thirdName: " + thirdName;
    }
}
