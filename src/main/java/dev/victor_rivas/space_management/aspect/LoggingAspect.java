package dev.victor_rivas.space_management.aspect;

import dev.victor_rivas.space_management.logging.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut para todos los métodos de controladores
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    /**
     * Pointcut para todos los métodos de servicios
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    /**
     * Log de entrada y salida de métodos de controladores
     */
    @Around("controllerMethods()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("CONTROLLER_ENTER | class={} | method={} | args={}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            log.debug("CONTROLLER_EXIT | class={} | method={} | duration={}ms",
                    className, methodName, duration);

            return result;
        } catch (Exception e) {
            log.error("CONTROLLER_ERROR | class={} | method={} | error={}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Log de entrada y salida de métodos de servicios
     */
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("SERVICE_ENTER | class={} | method={}", className, methodName);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            log.debug("SERVICE_EXIT | class={} | method={} | duration={}ms",
                    className, methodName, duration);

            return result;
        } catch (Exception e) {
            log.error("SERVICE_ERROR | class={} | method={} | error={}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Log específico para excepciones
     */
    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        LoggingUtils.logError(
                className + "." + methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                (Exception) exception
        );
    }
}
