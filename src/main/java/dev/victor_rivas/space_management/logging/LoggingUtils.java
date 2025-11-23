package dev.victor_rivas.space_management.logging;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
@UtilityClass
public class LoggingUtils {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";

    /**
     * Genera y establece un ID único para la solicitud actual
     */
    public static String generateRequestId() {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);
        return requestId;
    }

    /**
     * Establece el contexto del usuario en MDC
     */
    public static void setUserContext(Long userId, String username) {
        if (userId != null) {
            MDC.put(USER_ID, userId.toString());
        }
        if (username != null) {
            MDC.put(USERNAME, username);
        }
    }

    /**
     * Limpia el contexto de MDC
     */
    public static void clearMDC() {
        MDC.clear();
    }

    /**
     * Obtiene el requestId actual
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Log estructurado para eventos de autenticación
     */
    public static void logAuthEvent(String event, String username, boolean success, String details) {
        if (success) {
            log.info("AUTH_EVENT | event={} | username={} | success={} | details={}",
                    event, username, success, details);
        } else {
            log.warn("AUTH_EVENT | event={} | username={} | success={} | details={}",
                    event, username, success, details);
        }
    }

    /**
     * Log estructurado para eventos de negocio
     */
    public static void logBusinessEvent(String event, String entityType, Long entityId, String action, String details) {
        log.info("BUSINESS_EVENT | event={} | entityType={} | entityId={} | action={} | details={}",
                event, entityType, entityId, action, details);
    }

    /**
     * Log estructurado para errores
     */
    public static void logError(String operation, String errorType, String message, Exception e) {
        log.error("ERROR | operation={} | errorType={} | message={} | exception={}",
                operation, errorType, message, e.getClass().getSimpleName(), e);
    }
}
