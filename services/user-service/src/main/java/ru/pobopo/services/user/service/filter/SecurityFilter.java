package ru.pobopo.services.user.service.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// todo вынести куда то в общее?

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    private final static String USER_LOGIN_HEADER = "Current-User-Login";
    private final static String USER_ID_HEADER = "Current-User-Id";
    private final static String USER_ROLES_HEADER = "Current-User-Roles";

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String userLogin = request.getHeader(USER_LOGIN_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);
        String userRoles = request.getHeader(USER_ROLES_HEADER);

        /*
        Возможная реализация. Не уверен, что она акутальна, тк мы на этапе валидации токена тянем все данные из бд.
        if (StringUtils.isNotBlank(userLogin)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.warn("Authorized user: " + userDetails.toString());
        } else {
            log.warn("Empty user credits headers!");
        }
        */

        if (StringUtils.isNotBlank(userLogin) && StringUtils.isNotBlank(userId)) {
            List<String> roles = StringUtils.isBlank(userRoles) ? new ArrayList<>() : List.of(userRoles.split(";"));
            UserDetails userDetails = new User(
                userLogin,
                userId,
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.warn("Authorized user: " + userDetails);
        } else {
            log.warn("Empty user credits headers!");
        }

        filterChain.doFilter(request, response);
    }
}
