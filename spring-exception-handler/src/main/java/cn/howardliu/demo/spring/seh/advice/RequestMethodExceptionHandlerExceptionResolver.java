package cn.howardliu.demo.spring.seh.advice;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-03-24
 */
public class RequestMethodExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    private final Map<ControllerAndMethodAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
            new LinkedHashMap<>();
    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
            new ConcurrentHashMap<>(64);

    public RequestMethodExceptionHandlerExceptionResolver() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        initExceptionHandlerMethodAdviceCache();
    }

    private void initExceptionHandlerMethodAdviceCache() {
        if (getApplicationContext() == null) {
            return;
        }

        List<ControllerAndMethodAdviceBean> adviceBeans =
                ControllerAndMethodAdviceBean.findAnnotatedBeanList(getApplicationContext());
        for (ControllerAndMethodAdviceBean adviceBean : adviceBeans) {
            Class<?> beanType = adviceBean.getBeanType();
            if (beanType == null) {
                throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
            }
            ExceptionHandlerMethodResolver resolver = new ExceptionHandlerMethodResolver(beanType);
            if (resolver.hasExceptionMappings()) {
                this.exceptionHandlerAdviceCache.put(adviceBean, resolver);
            }
        }

        if (logger.isDebugEnabled()) {
            int handlerSize = this.exceptionHandlerAdviceCache.size();
            if (handlerSize == 0) {
                logger.debug("ControllerAdvice beans: none");
            } else {
                logger.debug("ControllerAdvice beans: " + handlerSize + " @ExceptionHandler");
            }
        }
    }

    @Override
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
            Exception exception) {
        Class<?> handlerType = null;
        Method runMethod = null;

        if (handlerMethod != null) {
            // Local exception handler methods on the controller class itself.
            // To be invoked through the proxy, even in case of an interface-based proxy.
            handlerType = handlerMethod.getBeanType();
            runMethod = handlerMethod.getMethod();

            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethod(exception);
            if (method != null) {
                return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method);
            }
            // For advice applicability check below (involving base packages, assignable types
            // and annotation presence), use target class instead of interface-based proxy.
            if (Proxy.isProxyClass(handlerType)) {
                handlerType = AopUtils.getTargetClass(handlerMethod.getBean());
            }
        }

        for (Map.Entry<ControllerAndMethodAdviceBean, ExceptionHandlerMethodResolver> entry
                : this.exceptionHandlerAdviceCache.entrySet()) {
            ControllerAndMethodAdviceBean advice = entry.getKey();
            if (advice.isApplicableToBeanType(handlerType, runMethod)) {
                ExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethod(exception);
                if (method != null) {
                    return new ServletInvocableHandlerMethod(advice.resolveBean(), method);
                }
            }
        }

        return null;
    }
}
