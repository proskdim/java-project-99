package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {
    UserDTO create(UserCreateDTO data);

    void delete(Long id);

    UserDTO findById(Long id);

    List<UserDTO> getAll();

    UserDTO update(UserUpdateDTO data, Long id);
}
