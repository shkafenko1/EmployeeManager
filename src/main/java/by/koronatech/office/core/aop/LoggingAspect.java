package by.koronatech.office.core.aop;

import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(pointcut = "execution(* by.koronatech.office.api.controller..*(..))", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        logger.info("Successfully executed {} with args: {}, result: {}", method, Arrays.toString(args), result);
    }

    @AfterThrowing(pointcut = "execution(* by.koronatech.office.api.controller..*(..))", throwing = "ex")
    public void logError(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        if (ex instanceof ConstraintViolationException || ex instanceof MethodArgumentNotValidException) {
            logger.warn("Validation error in {} with args: {}: {}", method, Arrays.toString(args), ex.getMessage());
        } else {
            logger.error("Unexpected error in {} with args: {}: {}", method, Arrays.toString(args), ex.getMessage());
        }
    }
}