package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Контроллер приветствия", description = "Самый простой контроллер для проверки работоспособности приложения")
public class WelcomeController {

    @GetMapping("/welcome")
    @Operation(summary = "Возвращение строки приветствия", description = "Возвращает строку приветсвия")
    public String welcome() {
        return "Welcome to Spring";
    }
}
