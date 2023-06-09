package ru.pobopo.services.user.service.mapper;

import org.mapstruct.Mapper;
import ru.pobopo.services.user.service.dto.UserDto;
import ru.pobopo.services.user.service.entity.UserEntity;

@Mapper(componentModel = "spring", uses = UserTypeMapper.class)
public interface UserMapper {
    UserDto toDto(UserEntity user);
}
