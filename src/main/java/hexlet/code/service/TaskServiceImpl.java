package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    @Override
    public TaskDTO create(TaskCreateDTO data) {
        var task = taskMapper.map(data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return taskMapper.map(task);
    }

    @Override
    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec);
        return tasks.stream().map(taskMapper::map).toList();
    }

    @Override
    public TaskDTO update(TaskUpdateDTO data, Long id) {
        var task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        taskMapper.update(data, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }
}
