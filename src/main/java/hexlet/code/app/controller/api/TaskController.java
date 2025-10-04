package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.specification.TaskSpecification;
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
@RequestMapping("/api/tasks")
@Tag(name = "Контроллер задач", description = "Контроллер предназначен для создания задач которые выполняет пользователь")
public final class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification taskSpecification;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание задачи", description = "Позволяет создать новую задачу в приложении")
    TaskDTO create(@Valid @RequestBody TaskCreateDTO data) {
        var task = taskMapper.map(data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление задачи", description = "Позволяет удалить задачу из приложения по её идентификатору")
    void destroy(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка задач", description = "Позволяет получить список всех задач добавленных в приложение")
    ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec);
        var body = tasks.stream().map(taskMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по идентификатору", description = "Позволяет получить информацию об определенной задаче по идентификатору")
    TaskDTO show(@PathVariable Long id) {
        var task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return taskMapper.map(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление задачи", description = "Позволяет частично или полностью обновить информацию о задаче")
    TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO data) {
        var task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        taskMapper.update(data, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }
}
