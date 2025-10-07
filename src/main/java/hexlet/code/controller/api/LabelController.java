package hexlet.code.controller.api;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.service.LabelService;
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
@RequestMapping("/api/labels")
@AllArgsConstructor
@Tag(name = "Контроллер меток", description = "Метки – гибкая альтернатива категориям. Позволяют группировать задачи по разным признакам, например багам, фичам и так далее.")
public final class LabelController {

    private final LabelService labelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание метки", description = "Позволяет создать новую метку в приложении")
    LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
        return labelService.create(data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление метки", description = "Позволяет удалить метку из приложения по его идентификатору")
    void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка меток", description = "Позволяет получить список всех меток добавленных в приложение")
    ResponseEntity<List<LabelDTO>> index() {
        var body = labelService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение метки по идентификатору", description = "Позволяет получить информацию об определенной метки по его идентификатору")
    LabelDTO show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление метки", description = "Позволяет частично или полностью обновить информацию о метке")
    LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO data) {
        return labelService.update(data, id);
    }

}
