package hexlet.code.app.component;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedInitializer implements CommandLineRunner {

    private final String adminEmail = "hexlet@example.com";
    private final String adminPassword = "qwerty";

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private final PasswordEncoder passwordEncoder;

    public SeedInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void createTaskStatus(String name, String slug) {
        var status = new TaskStatus();
        status.setName(name);
        status.setSlug(slug);
        taskStatusRepository.save(status);
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);

            System.out.println("Создан администратор по умолчанию.");
        }

        seedTaskStatuses();
        System.out.println("Добавлены статусы.");
    }

    private void seedTaskStatuses() {
        createTaskStatus("Draft", "draft");
        createTaskStatus("ToReview", "to_review");
        createTaskStatus("ToBeFixed", "to_be_fixed");
        createTaskStatus("ToPublish", "to_publish");
        createTaskStatus("Published", "published");
    }
}
