package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDTO create(LabelCreateDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @Override
    public void delete(Long id) {
        labelRepository.deleteById(id);
    }

    @Override
    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return labelMapper.map(label);
    }

    @Override
    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();
        return labels.stream().map(labelMapper::map).toList();
    }

    @Override
    public LabelDTO update(LabelUpdateDTO data, Long id) {
        var label = labelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found"));
        labelMapper.update(data, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }
}