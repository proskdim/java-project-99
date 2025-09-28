package hexlet.code.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class TaskStatusCreateDTO {

    @NotNull @Size(min = 1) private String name;

    @NotNull @Size(min = 1) private String slug;

}
