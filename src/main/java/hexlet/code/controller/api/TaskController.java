package hexlet.code.controller.api;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Tag(name = "Контроллер задач", description = "Контроллер предназначен для создания задач которые выполняет пользователь")
public final class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание задачи", description = "Позволяет создать новую задачу в приложении")
    TaskDTO create(@Valid @RequestBody TaskCreateDTO data) {
        return taskService.create(data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление задачи", description = "Позволяет удалить задачу из приложения по её идентификатору")
    void destroy(@PathVariable Long id) {
        taskService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка задач", description = "Позволяет получить список всех задач добавленных в приложение")
    ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var body = taskService.getAll(params);
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по идентификатору", description = "Позволяет получить информацию об определенной задаче по идентификатору")
    TaskDTO show(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление задачи", description = "Позволяет частично или полностью обновить информацию о задаче")
    TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO data) {
        return taskService.update(data, id);
    }
}
