package ru.pobopo.services.user.service.controller;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pobopo.services.user.service.controller.objects.AuthRequest;
import ru.pobopo.services.user.service.controller.objects.TokenResponse;
import ru.pobopo.services.user.service.entity.Role;
import ru.pobopo.services.user.service.entity.UserEntity;
import javax.naming.AuthenticationException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;
import ru.pobopo.services.user.service.services.api.AuthService;
import ru.pobopo.services.user.service.controller.objects.UserDetailsResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/validate")
    public UserDetailsResponse validateToken(@RequestParam("token") String token)
        throws AuthenticationException, TokenExpiredException {
        UserEntity user = authService.validateToken(token);
        return new UserDetailsResponse(
            user.getLogin(),
            user.getId(),
            user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        );
    }

    @PostMapping
    public TokenResponse auth(@RequestBody AuthRequest request) throws AuthenticationException {
        return new TokenResponse(authService.authUser(request.getLogin(), request.getPassword()));
    }
}
