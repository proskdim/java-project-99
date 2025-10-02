package hexlet.code.app.controller.api;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.repository.LabelRepository;
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
public final class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroy(@PathVariable Long id) {
        labelRepository.deleteById(id);
    }

    @GetMapping
    ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll();
        var body = labels.stream().map(labelMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", Integer.toString(body.size())).body(body);
    }

    @GetMapping("/{id}")
    LabelDTO show(@PathVariable Long id) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return labelMapper.map(label);
    }

    @PutMapping("/{id}")
    LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO data) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        labelMapper.update(data, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

}
