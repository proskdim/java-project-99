package hexlet.code.controller.api;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Контроллер аутентификации пользователя", description = "Контроллер предназначен для осуществления входа пользователя в систему")
public final class SessionController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Аутентифицировать пользователя и выдать JWT-токен", description = "Принимает учётные данные пользователя (логин и пароль) и выполняет аутентификацию. "
            + "Если аутентификация проходит успешно, возвращает JWT-токен "
            + "В случае неверных учётных данных или отсутствия пользователя выбрасывается исключение "
            + "и возвращается ошибка 401 Unauthorized.")
    String create(@RequestBody AuthRequest authRequest) {
        var auth = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(auth);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
