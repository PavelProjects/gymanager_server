package ru.pobopo.services.user.service.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.pobopo.services.user.service.context.RequestContextHolder;

@Slf4j
@Component
public class TransactionFilter implements Filter {
    private static final String REQUEST_UUID_HEADER = "Request-Uuid";
    private static final String LOG_REQUEST = "[{}] REQUEST ({}) [{}]::{}";
    private static final String LOG_RESPONSE = "[{}] RESPONSE ({}) FINISHED";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUuid = request.getHeader(REQUEST_UUID_HEADER);
        RequestContextHolder.setRequestUuid(StringUtils.isBlank(requestUuid) ? UUID.randomUUID() : UUID.fromString(requestUuid));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info(LOG_REQUEST,
            RequestContextHolder.getRequestUuidString(),
            auth.getName(),
            request.getMethod(),
            request.getServletPath()
        );

        filterChain.doFilter(request, response);

        log.info(LOG_RESPONSE,
            RequestContextHolder.getRequestUuidString(),
            auth.getName()
        );
    }
}