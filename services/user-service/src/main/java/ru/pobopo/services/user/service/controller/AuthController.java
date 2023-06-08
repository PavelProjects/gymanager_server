package ru.pobopo.services.user.service.controller;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pobopo.services.user.service.controller.objects.AuthRequest;
import ru.pobopo.services.user.service.controller.objects.TokenResponse;
import ru.pobopo.services.user.service.entity.Role;
import ru.pobopo.services.user.service.entity.UserEntity;
import ru.pobopo.services.user.service.exceptions.BadTokenException;
import ru.pobopo.services.user.service.exceptions.NotAuthenticatedException;
import ru.pobopo.services.user.service.exceptions.TokenExpiredException;
import ru.pobopo.services.user.service.services.api.AuthService;
import ru.pobopo.shared.objects.BaseRequest;
import ru.pobopo.shared.objects.UserDetails;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/validate")
    public UserDetails validateToken(@PathVariable String token)
        throws NotAuthenticatedException, TokenExpiredException, BadTokenException {
        UserEntity user = authService.validateToken(token);
        return new UserDetails(
            user.getLogin(),
            user.getId(),
            user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        );
    }

    @PostMapping
    public TokenResponse auth(@RequestBody AuthRequest request) throws NotAuthenticatedException {
        return new TokenResponse(authService.authUser(request.getLogin(), request.getPassword()));
    }
}
