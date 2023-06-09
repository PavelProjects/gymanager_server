package ru.pobopo.services.user.service.services.api;

import ru.pobopo.services.user.service.entity.UserEntity;
import javax.naming.AuthenticationException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;

public interface AuthService {
    String authUser(String login, String password) throws AuthenticationException;
    UserEntity validateToken(String token) throws AuthenticationException, TokenExpiredException;
}
