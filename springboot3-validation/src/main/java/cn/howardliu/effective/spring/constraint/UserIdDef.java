package cn.howardliu.effective.spring.constraint;

import org.hibernate.validator.cfg.ConstraintDef;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2024-11-13
 */
public class UserIdDef extends ConstraintDef<UserIdDef, UserId> {

    public UserIdDef() {
        super(UserId.class);
    }
}
