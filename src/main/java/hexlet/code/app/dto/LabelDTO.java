package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class LabelDTO {
    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
}
