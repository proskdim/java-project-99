package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class TaskDTO {

    private Long id;

    private Integer index;

    private String createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    private String status;

    private Set<Long> taskLabelIds;
}