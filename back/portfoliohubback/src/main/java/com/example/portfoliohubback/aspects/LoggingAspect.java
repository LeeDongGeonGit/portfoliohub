package com.example.portfoliohubback.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //컨트롤러 메시지 로깅
    @Before("execution(* com.example.portfoliohubback.controller.*.*(..))")
    public void logControllerMethodsBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("컨트롤러 method 실행: ", methodName);
    }

    @AfterReturning(pointcut = "execution(* com.example.portfoliohubback.controller.*.*(..))", returning = "result")
    public void logControllerMethodsAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Controller method {} executed successfully with result: {}", methodName, result);
    }


    // 리포지토리 메서드 로깅
    @Before("execution(* com.example.portfoliohubback.repository.*.*(..))")
    public void logRepositoryMethodsBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Calling repository method: {}", methodName);
    }

    @AfterReturning(pointcut = "execution(* com.example.portfoliohubback.repository.*.*(..))", returning = "result")
    public void logRepositoryMethodsAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Repository method {} executed successfully with result: {}", methodName, result);
    }
}

