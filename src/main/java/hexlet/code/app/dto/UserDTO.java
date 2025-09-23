package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class UserDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String createdAt;
}
