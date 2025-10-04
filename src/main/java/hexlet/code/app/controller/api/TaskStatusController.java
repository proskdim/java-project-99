package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
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
@RequestMapping("/api/task_statuses")
@Tag(name = "Контроллер статусов задач", description = "Контроллер предназначен для взаимодействия со статусами задач")
public final class TaskStatusController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание статуса", description = "Позволяет создать новый статус в приложении")
    TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO data) {
        var taskStatus = taskStatusMapper.map(data);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление статуса", description = "Позволяет удалить статус из приложения по его идентификатору")
    void destroy(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка статусов", description = "Позволяет получить список статусов добавленных в приложение")
    ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatusList = taskStatusRepository.findAll();
        var body = taskStatusList.stream().map(taskStatusMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение статуса по идентификатору", description = "Позволяет получить информацию об определенном статусе по его идентификатору")
    TaskStatusDTO show(@PathVariable Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found"));
        return taskStatusMapper.map(taskStatus);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление статуса", description = "Позволяет частично или полностью обновить информацию о статусе")
    TaskStatusDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO data) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found"));
        taskStatusMapper.update(data, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }
}
