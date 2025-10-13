package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.password:qwerty}")
    private String adminPassword;

    @Value("${app.seed.admin.email:hexlet@example.com}")
    private String adminEmail;

    private void createLabel(String name) {
        var label = new Label();
        label.setName(name);

        if (labelRepository.findByName(label.getName()).isEmpty()) {
            labelRepository.save(label);
        }
    }

    private void createTaskStatus(String name, String slug) {
        var status = new TaskStatus();
        status.setName(name);
        status.setSlug(slug);

        if (taskStatusRepository.findBySlug(status.getSlug()).isEmpty()) {
            taskStatusRepository.save(status);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);

            log.info("Создан администратор по умолчанию.");
        }

        seedTaskStatuses();
        log.info("Добавлены статусы.");

        seedLabels();
        log.info("Добавлены метки.");
    }

    private void seedLabels() {
        createLabel("feature");
        createLabel("bug");
    }

    private void seedTaskStatuses() {
        createTaskStatus("Draft", "draft");
        createTaskStatus("ToReview", "to_review");
        createTaskStatus("ToBeFixed", "to_be_fixed");
        createTaskStatus("ToPublish", "to_publish");
        createTaskStatus("Published", "published");
    }
}
