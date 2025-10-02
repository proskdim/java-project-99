package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
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

    @Autowired
    private LabelRepository labelRepository;

    private final PasswordEncoder passwordEncoder;

    public SeedInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void createLabel(String name) {
        var label = new Label();
        label.setName(name);
        labelRepository.save(label);
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

        seedLabels();
        System.out.println("Добавлены метки.");
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
