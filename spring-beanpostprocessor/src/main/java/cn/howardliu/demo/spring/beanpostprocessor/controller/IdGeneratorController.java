package cn.howardliu.demo.spring.beanpostprocessor.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.demo.spring.beanpostprocessor.annotation.IdGeneratorClient;
import cn.howardliu.demo.spring.beanpostprocessor.generator.IdGenerator;

/**
 * ID生成器Controller
 *
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * @version 1.0
 * @since 2021/4/22 下午5:25
 */
@RestController
@RequestMapping("id")
public class IdGeneratorController {
    @IdGeneratorClient
    private IdGenerator defaultIdGenerator;
    @IdGeneratorClient("name1")
    private IdGenerator name1IdGenerator;
    @IdGeneratorClient("name2")
    private IdGenerator name2IdGenerator;

    @GetMapping("list")
    public Map<String, Long> listId(@RequestParam(value = "name", required = false) String name) {
        final Map<String, Long> result = new HashMap<>();
        if (name == null || name.isEmpty()) {
            result.put(defaultIdGenerator.groupName(), defaultIdGenerator.nextId());
            return result;
        }
        switch (name) {
            case "name1":
                result.put(name1IdGenerator.groupName(), name1IdGenerator.nextId());
                break;
            case "name2":
                result.put(name2IdGenerator.groupName(), name2IdGenerator.nextId());
                break;
            default:
                result.put(defaultIdGenerator.groupName(), defaultIdGenerator.nextId());
                result.put(name1IdGenerator.groupName(), name1IdGenerator.nextId());
                result.put(name2IdGenerator.groupName(), name2IdGenerator.nextId());
                break;
        }
        return result;
    }
}
