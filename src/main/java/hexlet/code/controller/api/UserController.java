package hexlet.code.controller.api;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import hexlet.code.util.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "Контроллер пользователя", description = "Контроллер предназначен для взаимодействия с пользователями")
public class UserController {

    private static final String CURRENT_USER = "@userUtils.getCurrentUser().getId() == #id";
    private final UserService userService;
    private final UserUtils userUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание пользователя", description = "Позволяет создать нового пользователя в приложении")
    UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        return userService.create(data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление пользователя", description = "Позволяет удалить пользователя из приложения по его идентификатору")
    @PreAuthorize(CURRENT_USER)
    void destroy(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка пользователей", description = "Позволяет получить список всех пользователей добавленных в приложение")
    ResponseEntity<List<UserDTO>> index() {
        var body = userService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя по идентификатору", description = "Позволяет получить информацию об определенном пользователе по его идентификатору")
    UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление пользователя", description = "Позволяет частично или полностью обновить информацию о пользователе")
    @PreAuthorize(CURRENT_USER)
    UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
        return userService.update(data, id);
    }
}
