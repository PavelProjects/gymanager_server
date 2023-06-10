package ru.pobopo.services.gateway.service;

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import ru.pobopo.services.gateway.objects.UserInfo;

@Service
public class AuthenticationService {
    public UserInfo validateToken(String token) {
        return new UserInfo("test_name", "test_passowrd", new ArrayList<>());
    }
}
