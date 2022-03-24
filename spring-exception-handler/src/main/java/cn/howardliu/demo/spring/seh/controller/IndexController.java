package cn.howardliu.demo.spring.seh.controller;

import static cn.howardliu.demo.spring.seh.exception.CommonResponseEnum.INTERNAL_ERROR;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.howardliu.demo.spring.seh.advice.CheckController;
import cn.howardliu.demo.spring.seh.exception.CodeBaseException;
import cn.howardliu.demo.spring.seh.exception.CommonResponseEnum;
import cn.howardliu.demo.spring.seh.pojo.Response;
import cn.howardliu.demo.spring.seh.service.IndexService;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>created at 2020/8/24
 *
 * @author liuxh
 * @since 1.0.0
 */
@RestController
@RequestMapping("index")
@Slf4j
public class IndexController {
    private final IndexService indexService;

    public IndexController(final IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("hello1")
    public Response<String> hello1() {
        Response<String> response = new Response<>();
        try {
            response.setCode(200);
            response.setDesc("请求成功");
            response.setData(indexService.hello());
        } catch (Exception e) {
            log.error("hello1方法请求异常", e);
            response.setCode(500);
            response.setDesc("请求异常:" + e.getMessage());
        } finally {
            log.info("执行controller的finally结构");
        }
        return response;
    }

    @GetMapping("hello2")
    @CheckController
    public Response<String> hello2(@RequestParam("ex") String ex) {
        switch (ex) {
            case "ex1":
                throw new CodeBaseException(CommonResponseEnum.USER_NOT_FOUND, "用户信息不存在");
            case "ex2":
                throw new CodeBaseException(CommonResponseEnum.MENU_NOT_FOUND, "菜单信息不存在");
            case "ex3":
                throw new CodeBaseException(CommonResponseEnum.ILLEGAL_ARGUMENT, "请求参数异常");
            case "ex4":
                throw new CodeBaseException(CommonResponseEnum.DATA_NOT_FOUND, "数据不存在");
        }
        throw new CodeBaseException(INTERNAL_ERROR, new Object[]{ex}, "请求异常", new RuntimeException("运行时异常信息"));
    }

    @GetMapping("hello3")
    public Response<String> hello3(@RequestParam("ex") String ex) {
        switch (ex) {
            case "ex1":
                throw new CodeBaseException(CommonResponseEnum.USER_NOT_FOUND, "用户信息不存在");
            case "ex2":
                throw new CodeBaseException(CommonResponseEnum.MENU_NOT_FOUND, "菜单信息不存在");
            case "ex3":
                throw new CodeBaseException(CommonResponseEnum.ILLEGAL_ARGUMENT, "请求参数异常");
            case "ex4":
                throw new CodeBaseException(CommonResponseEnum.DATA_NOT_FOUND, "数据不存在");
        }
        throw new CodeBaseException(INTERNAL_ERROR, new Object[]{ex}, "请求异常", new RuntimeException("运行时异常信息"));
    }
}
