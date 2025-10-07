package hexlet.code.controller.api;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Контроллер пользователя", description = "Контроллер предназначен для взаимодействия с пользователями")
public final class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание пользователя", description = "Позволяет создать нового пользователя в приложении")
    UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        var user = userMapper.map(data);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление пользователя", description = "Позволяет удалить пользователя из приложения по его идентификатору")
    void destroy(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка пользователей", description = "Позволяет получить список всех пользователей добавленных в приложение")
    ResponseEntity<List<UserDTO>> index() {
        var users = userRepository.findAll();
        var body = users.stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя по идентификатору", description = "Позволяет получить информацию об определенном пользователе по его идентификатору")
    UserDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return userMapper.map(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление пользователя", description = "Позволяет частично или полностью обновить информацию о пользователе")
    UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        userMapper.update(data, user);
        userRepository.save(user);
        return userMapper.map(user);
    }
}
