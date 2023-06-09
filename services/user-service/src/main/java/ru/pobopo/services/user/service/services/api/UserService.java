package ru.pobopo.services.user.service.services.api;

import javax.naming.AuthenticationException;
import ru.pobopo.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;
import ru.pobopo.services.user.service.exceptions.BadRequestException;

public interface UserService {
    UserEntity getUserByLogin(String login);
    UserEntity getUserById(String id);
    UserEntity createUser(CreateUserRequest request) throws BadRequestException;
    void updateUser(UpdateUserRequest request)
        throws BadRequestException, AccessDeniedException, AuthenticationException;
}
