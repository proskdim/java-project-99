package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
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
class TaskStatusControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testTaskStatus = Instancio.create(modelGenerator.taskStatusModel());
    }

    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        var json = objectMapper.writeValueAsString(testTaskStatus);
        var request = post("/api/task_statuses").contentType(MediaType.APPLICATION_JSON).content(json);
        var response = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        var body = response.getResponse().getContentAsString();
        var taskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(taskStatus.getId()),
                v -> v.node("name").isEqualTo(taskStatus.getName()),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug()));
    }

    @Test
    public void testCreateUnauthorized() throws Exception {
        var request = post("/api/task_statuses").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser
    public void testDestroy() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId());
        assertThat(taskStatus).isEmpty();
    }

    @Test
    public void testDestroyUnauthorized() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId());
        mockMvc.perform(request).andExpect(status().isUnauthorized());

        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId());
        assertThat(taskStatus).isPresent();
    }

    @Test
    @WithMockUser
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isOk());
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testShow() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var response = mockMvc.perform(get("/api/task_statuses/" + testTaskStatus.getId())).andExpect(status().isOk())
                .andReturn();
        var body = response.getResponse().getContentAsString();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(testTaskStatus.getId()),
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug()));
    }

    @Test
    public void testShowUnauthorized() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        mockMvc.perform(get("/api/task_statuses/" + testTaskStatus.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var data = Map.of("name", "FooBar");
        var request = put("/api/task_statuses/" + testTaskStatus.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(taskStatus.getName()).isEqualTo("FooBar");

        var body = response.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(taskStatus.getId()),
                v -> v.node("name").isEqualTo("FooBar"), v -> v.node("slug").isEqualTo(taskStatus.getSlug()));
    }

    @Test
    public void testUpdateUnauthorized() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var data = Map.of("name", "FooBar");
        var request = put("/api/task_statuses/" + testTaskStatus.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

}