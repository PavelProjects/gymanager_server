package ru.pobopo.gymanager.services.user.service.services.impl;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pobopo.gymanager.services.user.service.context.RequestContextHolder;
import ru.pobopo.gymanager.services.user.service.exception.BadCredentialsException;
import ru.pobopo.gymanager.services.user.service.exception.TokenExpiredException;
import ru.pobopo.gymanager.services.user.service.repository.UserRepository;
import ru.pobopo.gymanager.services.user.service.services.JwtTokenService;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;
import javax.naming.AuthenticationException;
import ru.pobopo.gymanager.services.user.service.services.api.AuthService;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(
        JwtTokenService jwtTokenService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String authUser(String login, String password) throws AuthenticationException, BadCredentialsException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        UserEntity user = getUserAndValidate(login);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setAuthAttempts(user.getAuthAttempts() + 1);
            userRepository.save(user);
            log.warn(
                "[{}] Failed attempt to auth (wrong password): {} / {}",
                RequestContextHolder.getRequestId(),
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
