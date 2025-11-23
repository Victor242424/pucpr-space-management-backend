package dev.victor_rivas.space_management.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generar requestId
        String requestId = LoggingUtils.generateRequestId();

        // Obtener información del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            LoggingUtils.setUserContext(null, username);
        }

        // Log de entrada de la solicitud
        log.info("REQUEST_START | requestId={} | method={} | uri={} | remoteAddr={}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        // Guardar tiempo de inicio
        request.setAttribute("startTime", System.currentTimeMillis());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // Puede dejarse vacío o agregar lógica adicional
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // Calcular duración de la solicitud
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        // Log de finalización de la solicitud
        log.info("REQUEST_END | requestId={} | method={} | uri={} | status={} | duration={}ms",
                LoggingUtils.getRequestId(),
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

        // Limpiar MDC
        LoggingUtils.clearMDC();
    }
}
