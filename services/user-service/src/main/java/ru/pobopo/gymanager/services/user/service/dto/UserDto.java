package ru.pobopo.gymanager.services.user.service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String login;
    private String name;
    private String phone;
    private String email;
    private UserTypeDto type;
    private LocalDateTime creationDate;
    private boolean active;
}
