package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO create(UserCreateDTO data) {
        var user = userMapper.map(data);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO findById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return userMapper.map(user);
    }

    @Override
    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::map).toList();
    }

    @Override
    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        userMapper.update(data, user);
        userRepository.save(user);
        return userMapper.map(user);
    }
}
