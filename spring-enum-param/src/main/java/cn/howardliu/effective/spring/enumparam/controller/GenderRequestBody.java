package cn.howardliu.effective.spring.enumparam.controller;

import cn.howardliu.effective.spring.enumparam.enums.GenderIdCodeEnum;
import lombok.Data;

/**
 * @author HowardLiu <howardliu1988@163.com>
 * Created on 2021-08-03
 */
@Data
public class GenderRequestBody {
    private String name;
    private GenderIdCodeEnum gender;
    private long timestamp;
}
