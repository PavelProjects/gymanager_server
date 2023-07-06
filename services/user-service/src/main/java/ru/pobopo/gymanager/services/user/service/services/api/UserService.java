package ru.pobopo.gymanager.services.user.service.services.api;

import javax.naming.AuthenticationException;
import ru.pobopo.gymanager.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.gymanager.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;
import ru.pobopo.gymanager.services.user.service.exception.AccessDeniedException;
import ru.pobopo.gymanager.services.user.service.exception.BadRequestException;

public interface UserService {
    UserEntity getUserByLogin(String login);
    UserEntity getUserById(String id);
    UserEntity createUser(CreateUserRequest request) throws BadRequestException;
    void updateUser(UpdateUserRequest request)
        throws BadRequestException, AccessDeniedException, AuthenticationException;
}
