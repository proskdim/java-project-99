package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class TaskStatusDTO {

    private Long id;

    private String name;

    private String slug;

    private String createdAt;

}
