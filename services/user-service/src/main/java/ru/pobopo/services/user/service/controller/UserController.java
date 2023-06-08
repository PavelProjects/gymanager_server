package ru.pobopo.services.user.service.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pobopo.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.services.user.service.dto.UserDto;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;
import ru.pobopo.services.user.service.exceptions.BadRequestException;
import ru.pobopo.services.user.service.mapper.UserMapper;
import ru.pobopo.services.user.service.services.api.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public UserDto getUser(
        // Проверка на права?
//        @PathVariable String currentUserLogin,
//        @PathVariable String currentUserId,
        @PathVariable String login,
        @PathVariable String id
    ) throws BadRequestException {
        UserEntity user;
        if (StringUtils.isNotBlank(login)) {
            user = userService.getUserByLogin(login);
        } else if (StringUtils.isNotBlank(id)) {
            user = userService.getUserById(id);
        } else {
            throw new BadRequestException("Id and login is missing!");
        }
        if (user == null) {
            return new UserDto();
        }
        return userMapper.toDto(user);
    }

    @PostMapping("/create")
    public UserDto createUser(@RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request);
        return userMapper.toDto(user);
    }

    @PutMapping("/update")
    public void updateUser(@RequestBody UpdateUserRequest request) throws AccessDeniedException, BadRequestException {
        userService.updateUser(request);
    }
}
