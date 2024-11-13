package cn.howardliu.effective.spring.config.validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.cfg.defs.NotNullDef;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import cn.howardliu.effective.spring.constraint.UserIdDef;
import cn.howardliu.effective.spring.entity.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2024-11-13
 */
@Configuration
class ValidationConf {

    @Bean
    Validator validator(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        final HibernateValidatorConfiguration conf = Validation.byProvider(HibernateValidator.class).configure();
        final ConstraintMapping constraintMapping = conf.createConstraintMapping();

        final TypeConstraintMappingContext<User> context = constraintMapping.type(User.class);
        context.field("id").constraint(new UserIdDef());
        final NotNullDef notNullDef = new NotNullDef();
        notNullDef.message("must not be null");
        context.field("name").constraint(notNullDef);

        return conf.allowOverridingMethodAlterParameterConstraint(true)
                .addMapping(constraintMapping)
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory()
                .getValidator();
    }
}
