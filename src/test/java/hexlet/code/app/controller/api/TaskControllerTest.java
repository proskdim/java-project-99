package hexlet.code.app.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Task testTask;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testTask = Instancio.create(modelGenerator.taskModel());
        userRepository.save(testTask.getAssignee());
        taskStatusRepository.save(testTask.getTaskStatus());
    }

    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        var json = objectMapper.writeValueAsString(taskMapper.map(testTask));
        var request = post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(json);
        var response = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        var body = response.getResponse().getContentAsString();
        var task = taskRepository.findByName(testTask.getName()).get();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(task.getId()),
                v -> v.node("assignee_id").isEqualTo(task.getAssignee().getId()),
                v -> v.node("title").isEqualTo(task.getName()), v -> v.node("content").isEqualTo(task.getDescription()),
                v -> v.node("status").isEqualTo(task.getTaskStatus().getSlug()));
    }

    @Test
    public void testCreateUnauthorized() throws Exception {
        var request = post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask));
        mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser
    public void testDestroy() throws Exception {
        taskRepository.save(testTask);
        var request = delete("/api/tasks/" + testTask.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());

        var taskStatus = taskRepository.findById(testTask.getId());
        assertThat(taskStatus).isEmpty();
    }

    @Test
    public void testDestroyUnauthorized() throws Exception {
        taskRepository.save(testTask);
        var request = delete("/api/tasks/" + testTask.getId());
        mockMvc.perform(request).andExpect(status().isUnauthorized());

        var task = taskRepository.findById(testTask.getId());
        assertThat(task).isPresent();
    }

    @Test
    @WithMockUser
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isOk());
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testShow() throws Exception {
        taskRepository.save(testTask);
        var response = mockMvc.perform(get("/api/tasks/" + testTask.getId())).andExpect(status().isOk()).andReturn();
        var body = response.getResponse().getContentAsString();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()));
    }

    @Test
    public void testShowUnauthorized() throws Exception {
        taskRepository.save(testTask);
        mockMvc.perform(get("/api/tasks/" + testTask.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);
        var data = Map.of("title", "FooBar");
        var request = put("/api/tasks/" + testTask.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo("FooBar");

        var body = response.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(task.getId()), v -> v.node("title").isEqualTo("FooBar"),
                v -> v.node("content").isEqualTo(task.getDescription()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()));
    }

    @Test
    public void testUpdateUnauthorized() throws Exception {
        taskRepository.save(testTask);
        var data = Map.of("name", "FooBar");
        var request = put("/api/tasks/" + testTask.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }
}
