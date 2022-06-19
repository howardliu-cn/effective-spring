package cn.howardliu.effective.spring.springbootmessages.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-06-07
 */
@RestController
public class HelloController {
    @Autowired
    private MessageSource messageSource;

    @GetMapping("m1")
    public List<String> m1(Locale locale) {
        final List<String> multi = new ArrayList<>();
        multi.add(messageSource.getMessage("message.code1", null, locale));
        multi.add(messageSource.getMessage("message.code2", null, locale));
        multi.add(messageSource.getMessage("message.code3", null, locale));
        multi.add(messageSource.getMessage("message.code4", null, locale));
        multi.add(messageSource.getMessage("message.code5", null, locale));
        multi.add(messageSource.getMessage("message.code6", null, locale));
        return multi;
    }

    @GetMapping("m2")
    public List<String> m2(Locale locale) {
        final List<String> multi = new ArrayList<>();
        multi.add("参数为null: " + messageSource.getMessage("message.multiVars", null, locale));
        multi.add("参数为空: " + messageSource.getMessage("message.multiVars", new Object[]{}, locale));
        multi.add("只传一个参数: " + messageSource.getMessage("message.multiVars", new Object[]{"第一个参数"}, locale));
        multi.add("传两个参数: " + messageSource.getMessage("message.multiVars", new Object[]{"第一个参数", "第二个参数"}, locale));
        multi.add("传超过两个参数: " + messageSource.getMessage("message.multiVars", new Object[]{"第一个参数", "第二个参数", "第三个参数"}, locale));
        return multi;
    }

    @GetMapping("m3")
    public List<String> m3(Locale locale) {
        final List<String> multi = new ArrayList<>();
        multi.add("不存在的code: " + messageSource.getMessage("message.notExist", null, locale));
        return multi;
    }
}
