package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
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
public final class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        var user = userMapper.map(data);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroy(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @GetMapping
    ResponseEntity<List<UserDTO>> index() {
        var users = userRepository.findAll();
        var body = users.stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    UserDTO show(@PathVariable Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return userMapper.map(user);
    }

    @PutMapping("/{id}")
    UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        userMapper.update(data, user);
        userRepository.save(user);
        return userMapper.map(user);
    }
}
