package ru.pobopo.gymanager.services.user.service.services.api;

import ru.pobopo.gymanager.services.user.service.exception.BadCredentialsException;
import ru.pobopo.gymanager.services.user.service.exception.TokenExpiredException;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;
import javax.naming.AuthenticationException;

public interface AuthService {
    String authUser(String login, String password) throws AuthenticationException, BadCredentialsException;
    UserEntity validateToken(String token) throws AuthenticationException, TokenExpiredException;
}
