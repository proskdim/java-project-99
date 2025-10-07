package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public final class TaskCreateDTO {

    private Integer index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotBlank @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @NotNull private String status;

    private Set<Long> taskLabelIds;
}
