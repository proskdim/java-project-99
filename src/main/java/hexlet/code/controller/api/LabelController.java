package hexlet.code.controller.api;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
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
@RequestMapping("/api/labels")
@Tag(name = "Контроллер меток", description = "Метки – гибкая альтернатива категориям. Позволяют группировать задачи по разным признакам, например багам, фичам и так далее.")
public final class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание метки", description = "Позволяет создать новую метку в приложении")
    LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление метки", description = "Позволяет удалить метку из приложения по его идентификатору")
    void destroy(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Получение списка меток", description = "Позволяет получить список всех меток добавленных в приложение")
    ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll();
        var body = labels.stream().map(labelMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение метки по идентификатору", description = "Позволяет получить информацию об определенной метки по его идентификатору")
    LabelDTO show(@PathVariable Long id) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return labelMapper.map(label);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление метки", description = "Позволяет частично или полностью обновить информацию о метке")
    LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO data) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        labelMapper.update(data, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

}
