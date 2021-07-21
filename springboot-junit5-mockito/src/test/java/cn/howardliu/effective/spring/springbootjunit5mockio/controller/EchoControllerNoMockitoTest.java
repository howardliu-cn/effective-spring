package cn.howardliu.effective.spring.springbootjunit5mockio.controller;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.howardliu.effective.spring.springbootjunit5mockio.SpringbootJunit5MockioApplication;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-21
 */
@SpringBootTest(classes = SpringbootJunit5MockioApplication.class)
@AutoConfigureMockMvc
class EchoControllerNoMockitoTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void echo() throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.get("/echo/")
                        .param("name", "看山")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals("Hello, 看山", result);
    }
}
