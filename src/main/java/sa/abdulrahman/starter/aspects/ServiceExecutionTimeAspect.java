package sa.abdulrahman.starter.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
public class ServiceExecutionTimeAspect {

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = method.getName();

        long startTime = System.currentTimeMillis();
        
        try {
            // Execute the method
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Execution time of {}.{}(): {} ms", className, methodName, duration);
            
            return result;
        } catch (Throwable throwable) {
            // Log execution time even if method throws an exception
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("Execution time of {}.{}() (with exception): {} ms", className, methodName, duration);

            throw throwable;
        }
    }
}