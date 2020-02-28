package com.renxl.sharding.aop;

import com.renxl.sharding.annotation.RenxlRoute;
import lombok.Data;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;
@Data
public class RenxlRouteAdvisor  extends AbstractPointcutAdvisor {

    private Advice advice;

    private Pointcut pointcut;

    @Override
    public void setOrder(int order) {
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return null;
    }

    /**
     * 进行分库分表拦截的aop
     */
    public RenxlRouteAdvisor() {
        this.pointcut = buildPointcut();
    }

    /**
     * 所有指定RenxlRoute的方法会被进行分库分表
     * @return
     */
    private Pointcut buildPointcut() {
        ComposablePointcut composablePointcut = null;

        Pointcut cpc = new AnnotationMatchingPointcut(RenxlRoute.class, true);
        //方法级别
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(RenxlRoute.class);

        if (composablePointcut == null) {
            composablePointcut = new ComposablePointcut(cpc);
        }
        // 并集
         return composablePointcut.union(mpc);
    }
}
