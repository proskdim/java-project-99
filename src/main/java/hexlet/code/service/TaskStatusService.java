package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO create(TaskStatusCreateDTO data);

    void delete(Long id);

    TaskStatusDTO findById(Long id);

    List<TaskStatusDTO> getAll();

    TaskStatusDTO update(TaskStatusUpdateDTO data, Long id);
}
