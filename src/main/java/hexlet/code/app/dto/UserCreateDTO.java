package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public final class UserCreateDTO {

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Email private String email;

    @NotNull @Size(min = 3) private String password;

}
