package cn.howardliu.effective.spring.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.howardliu.effective.spring.entity.User;
import jakarta.validation.ConstraintViolationException;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2024-11-13
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void provideInvalidUser() {
        final User user = new User();
        user.setId(-1L);
        user.setName(null);

        Assertions.assertThatThrownBy(() -> userService.handleUser(user))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("handleUser.arg0.id: -1 must be a positive long")
                .hasMessageContaining("handleUser.arg0.name: must not be null");
    }

    @Test
    void provideValidUser() {
        final User user = new User();
        user.setId(1L);
        user.setName("howardliu.cn");

        assertDoesNotThrow(() -> userService.handleUser(user));
    }
}
