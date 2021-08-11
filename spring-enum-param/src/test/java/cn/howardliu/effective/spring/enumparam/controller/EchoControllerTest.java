package cn.howardliu.effective.spring.enumparam.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.howardliu.effective.spring.enumparam.SpringEnumParamApplication;
import cn.howardliu.effective.spring.enumparam.enums.GenderIdCodeEnum;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-07-20
 */
@SpringBootTest(classes = SpringEnumParamApplication.class)
@AutoConfigureMockMvc
class EchoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "male", "1"})
    void genderIdCode(String gender) throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.get("/echo/gender-id-code")
                        .param("gender", gender)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("MALE", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "male"})
    void genderCode(String gender) throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.get("/echo/gender-code")
                        .param("gender", gender)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("MALE", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "1"})
    void genderId(String gender) throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.get("/echo/gender-id")
                        .param("gender", gender)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("MALE", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "male", "1"})
    void postGenderIdCode(String gender) throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.post("/echo/gender-id-code1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"gender\": \"" + gender + "\", \"name\": \"看山\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        final GenderRequestBody genderRequest = objectMapper.readValue(result, GenderRequestBody.class);
        Assertions.assertEquals(GenderIdCodeEnum.MALE, genderRequest.getGender());
        Assertions.assertEquals("看山", genderRequest.getName());
        Assertions.assertTrue(genderRequest.getTimestamp() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void postGenderIdCode2(String gender) throws Exception {
        final String result = mockMvc.perform(
                MockMvcRequestBuilders.post("/echo/gender-id-code1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"gender\": " + gender + ", \"name\": \"看山\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        final GenderRequestBody genderRequest = objectMapper.readValue(result, GenderRequestBody.class);
        Assertions.assertEquals(GenderIdCodeEnum.MALE, genderRequest.getGender());
        Assertions.assertEquals("看山", genderRequest.getName());
        Assertions.assertTrue(genderRequest.getTimestamp() > 0);
    }
}
