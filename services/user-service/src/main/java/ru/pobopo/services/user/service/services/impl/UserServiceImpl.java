package ru.pobopo.services.user.service.services.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.pobopo.services.user.service.context.RequestContextHolder;
import ru.pobopo.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.entity.UserTypeEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;
import ru.pobopo.services.user.service.exceptions.BadRequestException;
import ru.pobopo.services.user.service.repository.UserRepository;
import ru.pobopo.services.user.service.repository.UserTypeRepository;
import ru.pobopo.services.user.service.services.PermitService;
import ru.pobopo.services.user.service.services.api.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity getUserByLogin(String login) {
        UserEntity user = userRepository.findByLogin(login);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public UserEntity getUserById(String id) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Transactional
    @Override
    public UserEntity createUser(CreateUserRequest request) throws BadRequestException {
        Objects.requireNonNull(request);
        UserEntity user = getUserByLogin(request.getLogin());
        if (user != null) {
            throw new BadRequestException("User with login " + request.getLogin() + " already exists!");
        }

        UserTypeEntity typeEntity = userTypeRepository.findByName(request.getType());
        Objects.requireNonNull(typeEntity, "Can't find this user type");
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        user = new UserEntity();
        user.setLogin(request.getLogin());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setType(typeEntity);
        user.setCreationDate(LocalDateTime.now());

        UserEntity userEntity = userRepository.save(user);
        log.info("[{}] New user created: {}",
            RequestContextHolder.getRequestUuidString(), userEntity);
        return userEntity;
    }

    @Transactional
    @Override
    public void updateUser(UpdateUserRequest request)
        throws BadRequestException, AccessDeniedException, AuthenticationException {
        UserEntity user;
        if (StringUtils.isNotBlank(request.getId())) {
            user = getUserById(request.getId());
        } else if (StringUtils.isNotBlank(request.getLogin())) {
            user = getUserByLogin(request.getLogin());
        } else {
            throw new BadRequestException("Id or login must be filled");
        }
        if (user == null) {
            throw new NotFoundException("Can't find user with given id/login");
        }
        if (!PermitService.canEditUser(user)) {
            log.warn("[{}] User {} tried to edit user {}",
                RequestContextHolder.getRequestUuidString(), PermitService.getCurrentUserName(), user);
            throw new AccessDeniedException();
        }

        boolean haveChanges = false;
        if (StringUtils.isNotBlank(request.getName())) {
            user.setName(request.getName());
            haveChanges = true;
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
            haveChanges = true;
        }

        if (haveChanges) {
            userRepository.save(user);
        }
    }
}
