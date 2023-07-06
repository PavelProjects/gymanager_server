package ru.pobopo.gymanager.services.user.service.mapper;

import org.mapstruct.Mapper;
import ru.pobopo.gymanager.services.user.service.dto.UserDto;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;

@Mapper(componentModel = "spring", uses = UserTypeMapper.class)
public interface UserMapper {
    UserDto toDto(UserEntity user);
}
