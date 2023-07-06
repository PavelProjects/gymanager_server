package ru.pobopo.gymanager.services.user.service.mapper;

import org.mapstruct.Mapper;
import ru.pobopo.gymanager.services.user.service.entity.UserTypeEntity;
import ru.pobopo.gymanager.services.user.service.dto.UserTypeDto;

@Mapper(componentModel = "spring")
public interface UserTypeMapper {
    UserTypeDto toDto(UserTypeEntity entity);
    UserTypeEntity toEntity(UserTypeDto dto);
}
