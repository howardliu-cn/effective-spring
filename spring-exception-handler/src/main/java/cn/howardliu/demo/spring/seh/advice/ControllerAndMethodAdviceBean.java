package cn.howardliu.demo.spring.seh.advice;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.ControllerAdviceBean;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-03-24
 */
public class ControllerAndMethodAdviceBean {
    private final ControllerAdviceBean controllerAdviceBean;

    private final MethodHandlerTypePredicate beanTypePredicate;

    public ControllerAndMethodAdviceBean(Object bean) {
        this.controllerAdviceBean = new ControllerAdviceBean(bean);
        this.beanTypePredicate = createBeanTypePredicate(controllerAdviceBean.getBeanType());
    }

    public ControllerAndMethodAdviceBean(String beanName, BeanFactory beanFactory) {
        this(beanName, beanFactory, null);
    }

    public ControllerAndMethodAdviceBean(String beanName, BeanFactory beanFactory,
            ControllerAdvice controllerAdvice) {
        this.controllerAdviceBean = new ControllerAdviceBean(beanName, beanFactory, controllerAdvice);

        if (controllerAdvice == null) {
            this.beanTypePredicate = createBeanTypePredicate(controllerAdviceBean.getBeanType());
        } else {
            this.beanTypePredicate = createBeanTypePredicate(controllerAdvice);
        }
    }

    public int getOrder() {
        return controllerAdviceBean.getOrder();
    }

    public ControllerAdviceBean getControllerAdviceBean() {
        return this.controllerAdviceBean;
    }

    @Nullable
    public Class<?> getBeanType() {
        return controllerAdviceBean.getBeanType();
    }

    public Object resolveBean() {
        return controllerAdviceBean.resolveBean();
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        return isApplicableToBeanType(beanType, null);
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType, @Nullable Method method) {
        return this.beanTypePredicate.test(beanType, method);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControllerAndMethodAdviceBean)) {
            return false;
        }
        ControllerAndMethodAdviceBean otherAdvice = (ControllerAndMethodAdviceBean) other;
        return (this.controllerAdviceBean.equals(otherAdvice.controllerAdviceBean));
    }

    @Override
    public int hashCode() {
        return this.controllerAdviceBean.hashCode();
    }

    @Override
    public String toString() {
        return this.controllerAdviceBean.toString();
    }

    public static List<ControllerAndMethodAdviceBean> findAnnotatedBeanList(ApplicationContext context) {
        List<ControllerAndMethodAdviceBean> adviceBeans = new ArrayList<>();
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class)) {
            if (!ScopedProxyUtils.isScopedTarget(name)) {
                ControllerAdvice controllerAdvice = context.findAnnotationOnBean(name, ControllerAdvice.class);
                if (controllerAdvice != null) {
                    // Use the @ControllerAdvice annotation found by findAnnotationOnBean()
                    // in order to avoid a subsequent lookup of the same annotation.
                    adviceBeans.add(new ControllerAndMethodAdviceBean(name, context, controllerAdvice));
                }
            }
        }
        OrderComparator.sort(adviceBeans);
        return adviceBeans;
    }

    private static MethodHandlerTypePredicate createBeanTypePredicate(@Nullable Class<?> beanType) {
        ControllerAdvice controllerAdvice = (beanType != null
                                             ?
                                             findMergedAnnotation(beanType, ControllerAdvice.class) : null);
        return createBeanTypePredicate(controllerAdvice);
    }

    private static MethodHandlerTypePredicate createBeanTypePredicate(@Nullable ControllerAdvice controllerAdvice) {
        if (controllerAdvice != null) {
            return MethodHandlerTypePredicate.builder()
                    .basePackage(controllerAdvice.basePackages())
                    .basePackageClass(controllerAdvice.basePackageClasses())
                    .assignableType(controllerAdvice.assignableTypes())
                    .annotation(controllerAdvice.annotations())
                    .build();
        }
        return MethodHandlerTypePredicate.forAnyHandlerType();
    }
}
