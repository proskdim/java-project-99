package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var taskStatus = taskStatusMapper.map(data);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }

    @Override
    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found"));
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream().map(taskStatusMapper::map).toList();
    }

    @Override
    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found"));
        taskStatusMapper.update(data, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }
}
