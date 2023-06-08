package ru.pobopo.services.user.service.services.api;

import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.BadTokenException;
import ru.pobopo.services.user.service.exceptions.NotAuthenticatedException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;

public interface AuthService {
    String authUser(String login, String password) throws NotAuthenticatedException;
    UserEntity validateToken(String token) throws NotAuthenticatedException, BadTokenException, TokenExpiredException;
}
