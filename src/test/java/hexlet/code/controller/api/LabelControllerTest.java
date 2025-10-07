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
import hexlet.code.dto.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Label testLabel;

    @Autowired
    private LabelMapper labelMapper;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testLabel = Instancio.create(modelGenerator.labelModel());
    }

    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        var json = objectMapper.writeValueAsString(testLabel);
        var request = post("/api/labels").contentType(MediaType.APPLICATION_JSON).content(json);
        var response = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
        var body = response.getResponse().getContentAsString();
        var label = labelRepository.findByName(testLabel.getName()).get();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(label.getId()),
                v -> v.node("name").isEqualTo(label.getName()));
    }

    @Test
    public void testCreateUnauthorized() throws Exception {
        var request = post("/api/labels").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLabel));
        mockMvc.perform(request).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser
    public void testDestroy() throws Exception {
        labelRepository.save(testLabel);
        var request = delete("/api/labels/" + testLabel.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());
        var label = labelRepository.findById(testLabel.getId());
        assertThat(label).isEmpty();
    }

    @Test
    public void testDestroyUnauthorized() throws Exception {
        labelRepository.save(testLabel);
        var request = delete("/api/labels/" + testLabel.getId());
        mockMvc.perform(request).andExpect(status().isUnauthorized());

        var label = labelRepository.findById(testLabel.getId());
        assertThat(label).isPresent();
    }

    @Test
    @WithMockUser
    public void testIndex() throws Exception {
        labelRepository.save(testLabel);

        var result = mockMvc.perform(get("/api/labels").with(jwt())).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();

        List<LabelDTO> labelDTOS = objectMapper.readValue(body, new TypeReference<>() {
        });

        var actual = labelDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testShow() throws Exception {
        labelRepository.save(testLabel);
        var response = mockMvc.perform(get("/api/labels/" + testLabel.getId())).andExpect(status().isOk()).andReturn();
        var body = response.getResponse().getContentAsString();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName()));
    }

    @Test
    public void testShowUnauthorized() throws Exception {
        labelRepository.save(testLabel);
        mockMvc.perform(get("/api/labels/" + testLabel.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testUpdate() throws Exception {
        labelRepository.save(testLabel);
        var data = Map.of("name", "FooBar");
        var request = put("/api/labels/" + testLabel.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var label = labelRepository.findById(testLabel.getId()).get();
        assertThat(label.getName()).isEqualTo("FooBar");

        var body = response.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("id").isEqualTo(label.getId()), v -> v.node("name").isEqualTo("FooBar"));
    }

    @Test
    public void testUpdateUnauthorized() throws Exception {
        labelRepository.save(testLabel);
        var data = Map.of("name", "FooBar");
        var request = put("/api/labels/" + testLabel.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }
}
