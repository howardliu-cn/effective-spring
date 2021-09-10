package cn.howardliu.effective.spring.springbootjunit5mockio.controller;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.howardliu.effective.spring.springbootjunit5mockio.SpringbootJunit5MockioApplication;
import cn.howardliu.effective.spring.springbootjunit5mockio.service.EchoService;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-21
 */
@SpringBootTest(classes = SpringbootJunit5MockioApplication.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class EchoControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EchoService echoService;

    @BeforeEach
    void setUp() {
        Mockito.when(echoService.echo(Mockito.any()))
                .thenReturn("看山说: " + System.currentTimeMillis());
    }

    @Test
    void echo() throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.get("/echo/")
                        .param("name", "看山的小屋")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertTrue(result.startsWith("看山"));
    }
}
