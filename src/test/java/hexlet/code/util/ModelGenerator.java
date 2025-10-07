package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import java.util.Set;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ModelGenerator {

    @Autowired
    private Faker faker;

    public Model<Label> labelModel() {
        return Instancio.of(Label.class).ignore(Select.field(Label::getId)).toModel();
    }

    public Model<Task> taskModel() {
        return Instancio.of(Task.class).ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getAssignee), () -> Instancio.create(userModel()))
                .supply(Select.field(Task::getTaskStatus), () -> Instancio.create(taskStatusModel()))
                .supply(Select.field(Task::getLabels), () -> Set.of(Instancio.create(labelModel()))).toModel();
    }

    public Model<TaskStatus> taskStatusModel() {
        var name = faker.regexify("\\w{5,15}");
        var slug = name.toLowerCase();
        return Instancio.of(TaskStatus.class).ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> name)
                .supply(Select.field(TaskStatus::getSlug), () -> slug).toModel();
    }

    public Model<User> userModel() {
        return Instancio.of(User.class).ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress()).toModel();
    }
}