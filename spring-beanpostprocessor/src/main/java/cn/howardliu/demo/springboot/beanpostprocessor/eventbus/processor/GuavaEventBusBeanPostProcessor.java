package cn.howardliu.demo.springboot.beanpostprocessor.eventbus.processor;

import cn.howardliu.demo.springboot.beanpostprocessor.eventbus.annotation.Subscriber;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.function.BiConsumer;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/23 下午9:31
 */
@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class GuavaEventBusBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GuavaEventBusBeanPostProcessor.class);
    SpelExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        this.process(bean, EventBus::register, "initialization");
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        this.process(bean, EventBus::unregister, "destruction");
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return true;
    }

    private void process(Object bean, BiConsumer<EventBus, Object> consumer, String action) {
        final Object proxy = this.getTargetObject(bean);
        final Subscriber annotation = AnnotationUtils.getAnnotation(proxy.getClass(), Subscriber.class);
        if (annotation == null) {
            return;
        }
        logger.info("{}: processing bean of type {} during {}", this.getClass().getSimpleName(), proxy.getClass().getName(), action);
        String annotationValue = annotation.value();
        try {
            final Expression expression = this.expressionParser.parseExpression(annotationValue);
            final Object value = expression.getValue();
            if (!(value instanceof EventBus)) {
                logger.error("{}: expression {} did not evaluate to an instance of EventBus for bean of type {}",
                        this.getClass().getSimpleName(), annotationValue, proxy.getClass().getSimpleName());
                return;
            }
            final EventBus eventBus = (EventBus) value;
            consumer.accept(eventBus, proxy);
        } catch (ExpressionException ex) {
            logger.error("{}: unable to parse/evaluate expression {} for bean of type {}",
                    this.getClass().getSimpleName(), annotationValue, proxy.getClass().getName());
        }
    }

    private Object getTargetObject(Object proxy) throws BeansException {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            try {
                return ((Advised) proxy).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FatalBeanException("Error getting target of JDK proxy", e);
            }
        }
        return proxy;
    }
}
