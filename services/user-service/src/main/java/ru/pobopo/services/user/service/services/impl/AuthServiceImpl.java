package ru.pobopo.services.user.service.services.impl;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.pobopo.services.user.service.context.RequestContextHolder;
import ru.pobopo.services.user.service.entity.UserEntity;
import javax.naming.AuthenticationException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;
import ru.pobopo.services.user.service.repository.UserRepository;
import ru.pobopo.services.user.service.services.JwtTokenService;
import ru.pobopo.services.user.service.services.api.AuthService;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(
        JwtTokenService jwtTokenService,
        UserRepository userRepository,
        AuthenticationManager authenticationManager
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String authUser(String login, String password) throws AuthenticationException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        UserEntity user = getUserAndValidate(login);

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, password));

        if (!auth.isAuthenticated()) {
            user.setAuthAttempts(user.getAuthAttempts() + 1);
            userRepository.save(user);
            log.warn(
                "[{}] Failed attempt to auth (wrong password): {} / {}",
                RequestContextHolder.getRequestUuidString(),
                login,
                password
            );
            throw new BadCredentialsException("Wrong user credits");
        }

        if (user.getAuthAttempts() > 0) {
            user.setAuthAttempts(0);
            userRepository.save(user);
        }
        return jwtTokenService.generateToken(user);
    }

    @Override
    public UserEntity validateToken(String token) throws AuthenticationException, TokenExpiredException {
        String login = jwtTokenService.validateAndGetLogin(token);
        return getUserAndValidate(login);
    }

    @NotNull
    private UserEntity getUserAndValidate(String login) throws AuthenticationException {
        UserEntity user = userRepository.findByLogin(login);
        if (user == null) {
            throw new AuthenticationException(String.format("Failed attempt to auth (no user found): %s", login));
        }

        if (!user.isActive()) {
            throw new AuthenticationException(String.format("Failed attempt to auth (inactive user): %s", login));
        }
        return user;
    }
}
