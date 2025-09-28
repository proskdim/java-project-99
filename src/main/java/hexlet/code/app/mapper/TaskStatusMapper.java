package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {
        JsonNullableMapper.class}, componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class TaskStatusMapper {

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);

}
