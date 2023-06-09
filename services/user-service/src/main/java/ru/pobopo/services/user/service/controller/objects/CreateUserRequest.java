package ru.pobopo.services.user.service.controller.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String id;
    private String login;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String type;
}
