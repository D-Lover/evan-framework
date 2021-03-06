package org.evanframework.syslog;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;

/**
 * <p/>
 * create at 2016年4月23日 下午8:57:59
 *
 * @author <a href="mailto:277469513@qq.com">Evan.Shen</a>
 * @version %I%, %G%
 */
public class MethodProfileInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invoction) throws Throwable {
        if (TimeProfiler.isProfileEnable()) {
            return invokeWithProfile(invoction);
        } else {
            return invoction.proceed();
        }

    }

    private Object invokeWithProfile(MethodInvocation invoction)
            throws Throwable {
        Method method = invoction.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        String className = ClassUtils.getShortClassName(clazz);
        TimeProfiler.beginTask(new StringBuilder(className).append(':').append(
                method.getName()).toString());
        Object ret;
        try {
            ret = invoction.proceed();
        } finally {
            TimeProfiler.endTask();
        }
        return ret;
    }
}