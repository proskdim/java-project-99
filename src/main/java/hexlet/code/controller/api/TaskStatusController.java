package hexlet.code.controller.api;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
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
@RequestMapping("/api/task_statuses")
@AllArgsConstructor
@Tag(name = "Контроллер статусов задач", description = "Контроллер предназначен для взаимодействия со статусами задач")
public final class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание статуса", description = "Позволяет создать новый статус в приложении")
    TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO data) {
        return taskStatusService.create(data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление статуса", description = "Позволяет удалить статус из приложения по его идентификатору")
    void destroy(@PathVariable Long id) {
        taskStatusService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка статусов", description = "Позволяет получить список статусов добавленных в приложение")
    ResponseEntity<List<TaskStatusDTO>> index() {
        var body = taskStatusService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение статуса по идентификатору", description = "Позволяет получить информацию об определенном статусе по его идентификатору")
    TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление статуса", description = "Позволяет частично или полностью обновить информацию о статусе")
    TaskStatusDTO update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO data) {
        return taskStatusService.update(data, id);
    }
}
