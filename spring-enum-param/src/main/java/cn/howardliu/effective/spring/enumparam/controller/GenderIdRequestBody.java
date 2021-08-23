package cn.howardliu.effective.spring.enumparam.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import cn.howardliu.effective.spring.enumparam.deserializer.IdCodeToEnumDeserializer;
import cn.howardliu.effective.spring.enumparam.enums.GenderIdEnum;
import lombok.Data;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-08-03
 */
@Data
public class GenderIdRequestBody {
    private String name;
    @JsonDeserialize(using = IdCodeToEnumDeserializer.class)
    private GenderIdEnum gender;
    private long timestamp;
}
