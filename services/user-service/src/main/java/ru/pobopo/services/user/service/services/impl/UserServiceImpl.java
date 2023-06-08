package ru.pobopo.services.user.service.services.impl;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.pobopo.services.user.service.controller.objects.CreateUserRequest;
import ru.pobopo.services.user.service.controller.objects.UpdateUserRequest;
import ru.pobopo.services.user.service.entity.Role;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.entity.UserTypeEntity;
import ru.pobopo.services.user.service.exceptions.AccessDeniedException;
import ru.pobopo.services.user.service.exceptions.BadRequestException;
import ru.pobopo.services.user.service.repository.UserRepository;
import ru.pobopo.services.user.service.repository.UserTypeRepository;
import ru.pobopo.services.user.service.services.PermitService;
import ru.pobopo.services.user.service.services.api.UserService;
import ru.pobopo.shared.objects.UserDetails;

@Service
@Log4j2
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
    public UserEntity createUser(CreateUserRequest request) {
        Objects.requireNonNull(request);

        UserTypeEntity typeEntity = userTypeRepository.findByName(request.getType());
        Objects.requireNonNull(typeEntity, "Can't find this user type");
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setLogin(request.getLogin());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setType(typeEntity);

        UserEntity userEntity = userRepository.save(user);
        log.info("New user created: " + userEntity);
        return userEntity;
    }

    @Override
    public UserEntity getUserByDetails(UserDetails details) {
        return userRepository.findByLoginAndId(details.getUserLogin(), details.getUserId());
    }

    @Transactional
    @Override
    public void updateUser(UpdateUserRequest request) throws BadRequestException, AccessDeniedException {
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
        UserEntity currentUser = getUserByDetails(request.getUserDetails());
        if (!PermitService.canEditUser(currentUser, user)) {
            log.warn(String.format("User %s tried to edit user %s", currentUser.toString(), user.toString()));
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
