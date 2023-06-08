package ru.pobopo.services.user.service.services.impl;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.BadTokenException;
import ru.pobopo.services.user.service.exceptions.NotAuthenticatedException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;
import ru.pobopo.services.user.service.repository.UserRepository;
import ru.pobopo.services.user.service.services.JwtTokenService;
import ru.pobopo.services.user.service.services.api.AuthService;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthServiceImpl(JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public String authUser(String login, String password) throws NotAuthenticatedException {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);

        UserEntity user = getUserAndValidate(login);

        String encodedPassword = passwordEncoder.encode(password);
        if (!StringUtils.equals(encodedPassword, user.getPassword())) {
            user.setAuthAttempts(user.getAuthAttempts() + 1);
            userRepository.save(user);
            log.warn(String.format("Failed attempt to auth (wrong password): %s / %s", login, password));
            throw new NotAuthenticatedException("Wrong password");
        }
        if (user.getAuthAttempts() > 0) {
            user.setAuthAttempts(0);
            userRepository.save(user);
        }
        return jwtTokenService.generateToken(user);
    }

    @Override
    public UserEntity validateToken(String token) throws NotAuthenticatedException, BadTokenException, TokenExpiredException {
        String login = jwtTokenService.validateAndGetLogin(token);
        return getUserAndValidate(login);
    }

    @NotNull
    private UserEntity getUserAndValidate(String login) throws NotAuthenticatedException{
        UserEntity user = userRepository.findByLogin(login);
        if (user == null) {
            log.warn(String.format("Failed attempt to auth (no user found): %s", login));
            throw new NotAuthenticatedException("Wrong login");
        }

        if (!user.isActive()) {
            log.warn(String.format("Failed attempt to auth (inactive user): %s", login));
            throw new NotAuthenticatedException("User is inactive");
        }
        return user;
    }
}
