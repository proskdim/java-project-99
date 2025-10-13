package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.extension.PostgresDbCleanerExtension;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ExtendWith(PostgresDbCleanerExtension.class)
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
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;

    private Task testTask;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testTask = Instancio.create(modelGenerator.taskModel());
        userRepository.save(testTask.getAssignee());
        taskStatusRepository.save(testTask.getTaskStatus());
        labelRepository.saveAll(testTask.getLabels());
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
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks").with(jwt())).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();

        List<TaskDTO> actualDTOs = objectMapper.readValue(body, new TypeReference<>() {
        });

        List<TaskDTO> expectedDTOs = taskRepository.findAll().stream().map(taskMapper::map).toList();

        assertThat(actualDTOs).usingRecursiveComparison().isEqualTo(expectedDTOs);
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testIndexWithFilterParams() throws Exception {
        taskRepository.save(testTask);
        var title = testTask.getName();
        var assigneeId = testTask.getAssignee().getId();
        var statusSlug = testTask.getTaskStatus().getSlug();
        var labelId = testTask.getLabels().stream().findFirst().get().getId();

        var url = "/api/tasks?titleCont=%s&assigneeId=%s&status=%s&labelId=%s".formatted(title, assigneeId, statusSlug,
                labelId);

        var result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().isNotEmpty().allSatisfy(el -> {
            assertThatJson(el).and(v -> v.node("title").isEqualTo(title))
                    .and(v -> v.node("assignee_id").isEqualTo(assigneeId))
                    .and(v -> v.node("status").isEqualTo(statusSlug))
                    .and(v -> v.node("taskLabelIds").isArray().contains(labelId));
        });
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