package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import java.util.List;

public interface LabelService {

    LabelDTO create(LabelCreateDTO data);

    void delete(Long id);

    LabelDTO findById(Long id);

    List<LabelDTO> getAll();

    LabelDTO update(LabelUpdateDTO data, Long id);
}