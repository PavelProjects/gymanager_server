package ru.pobopo.services.user.service.mapper;

import org.mapstruct.Mapper;
import ru.pobopo.services.user.service.dto.UserTypeDto;
import ru.pobopo.services.user.service.entity.UserTypeEntity;

@Mapper(componentModel = "spring")
public interface UserTypeMapper {
    UserTypeDto toDto(UserTypeEntity entity);
    UserTypeEntity toEntity(UserTypeDto dto);
}
