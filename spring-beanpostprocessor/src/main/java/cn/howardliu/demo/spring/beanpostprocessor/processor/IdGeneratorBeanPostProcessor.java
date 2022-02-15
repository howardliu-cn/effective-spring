package cn.howardliu.demo.spring.beanpostprocessor.processor;

import cn.howardliu.demo.spring.beanpostprocessor.annotation.IdGeneratorClient;
import cn.howardliu.demo.spring.beanpostprocessor.generator.IdGenerator;
import cn.howardliu.demo.spring.beanpostprocessor.generator.IdGeneratorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * @version 1.0
 * @since 2021/4/21 下午5:39
 */
@SuppressWarnings("NullableProblems")
public class IdGeneratorBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        parseFields(bean);
        return bean;
    }

    private void parseFields(final Object bean) {
        if (bean == null) {
            return;
        }
        Class<?> clazz = bean.getClass();
        parseFields(bean, clazz);

        while (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            clazz = clazz.getSuperclass();
            parseFields(bean, clazz);
        }
    }

    private void parseFields(final Object bean, Class<?> clazz) {
        if (bean == null || clazz == null) {
            return;
        }

        for (final Field field : clazz.getDeclaredFields()) {
            try {
                final IdGeneratorClient annotation = AnnotationUtils.getAnnotation(field, IdGeneratorClient.class);
                if (annotation == null) {
                    continue;
                }

                final String groupName = annotation.value();

                final Class<?> fieldType = field.getType();
                if (fieldType.equals(IdGenerator.class)) {
                    final IdGenerator idGenerator = IdGeneratorFactory.INSTANCE.create(groupName);
                    invokeSetField(bean, field, idGenerator);
                    continue;
                }

                throw new RuntimeException("未知字段类型无法初始化，bean: " + bean + "，field: " + field);
            } catch (Throwable t) {
                throw new RuntimeException("初始化字段失败，bean=" + bean + "，field=" + field, t);
            }
        }
    }

    private void invokeSetField(final Object bean, final Field field, final Object param) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, param);
    }
}
