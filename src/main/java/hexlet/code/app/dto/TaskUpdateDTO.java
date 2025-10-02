package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

public final class TaskUpdateDTO {

    @Setter
    @Getter
    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @Setter
    @Getter
    @NotBlank @JsonProperty("title")
    private JsonNullable<String> name;

    @Setter
    @Getter
    @JsonProperty("content")
    private JsonNullable<String> description;

    @Setter
    @Getter
    @NotNull private JsonNullable<String> status;

    @Setter
    @Getter
    private JsonNullable<Set<Long>> taskLabelIds;

    public @NotNull JsonNullable<Long> getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(@NotNull JsonNullable<Long> assigneeId) {
        this.assigneeId = assigneeId;
    }
}
