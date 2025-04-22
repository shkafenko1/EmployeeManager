package by.koronatech.office.core.aop;

import java.util.Arrays;
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
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* by.koronatech.office.api.controller.*.*(..)) "
            + "&& !execution(* by.koronatech.office.core.exceptions.GlobalExceptionHandler.*(..)) "
            + "|| execution(* by.koronatech.office.core.service.impl.*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        String args = Arrays.toString(joinPoint.getArgs());
        logger.debug("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(), args);
    }

    @AfterReturning(pointcut = "execution(* by.koronatech.office.api.controller.*.*(..)) "
            + "&& !execution(* by.koronatech.office.core.exceptions.GlobalExceptionHandler.*(..))"
            + "|| execution(* by.koronatech.office.core.service.impl.*.*(..))",
            returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        logger.debug("Exiting method: {} with result: {}",
                joinPoint.getSignature().toShortString(), result);
    }
}