package cn.howardliu.effective.spring.springaspect.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2021-08-10
 */
@Aspect
@Component
public class MethodSignatureAspect {
    @Pointcut("@annotation(cn.howardliu.effective.spring.springaspect.aspect.MethodSignature)")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().toLongString());
    }
}
