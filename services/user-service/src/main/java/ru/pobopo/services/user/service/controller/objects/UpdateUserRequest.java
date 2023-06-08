package ru.pobopo.services.user.service.controller.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pobopo.shared.objects.BaseRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest extends BaseRequest {
    private String id;
    private String login;
    private String name;
    private Boolean active;
}
