package by.koronatech.office.core.config;

import by.koronatech.office.core.service.VisitCounterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VisitCounterFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(VisitCounterFilter.class);
    private final VisitCounterService visitCounterService;

    public VisitCounterFilter(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if ("GET".equalsIgnoreCase(method) && uri.startsWith("/api/departments")) {
            logger.info("Incrementing visit counter for URI: {}", uri);
            visitCounterService.incrementVisit(uri);
        }

        chain.doFilter(request, response);
    }
}