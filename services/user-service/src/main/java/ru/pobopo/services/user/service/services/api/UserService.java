package ru.pobopo.services.user.service.services.api;

import ru.pobopo.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.services.user.service.dto.UserDto;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;
import ru.pobopo.services.user.service.exceptions.BadRequestException;
import ru.pobopo.shared.objects.UserDetails;

public interface UserService {
    UserEntity getUserByLogin(String login);
    UserEntity getUserById(String id);
    UserEntity createUser(CreateUserRequest request);
    void updateUser(UpdateUserRequest request) throws BadRequestException, AccessDeniedException;
    UserEntity getUserByDetails(UserDetails details);
}
