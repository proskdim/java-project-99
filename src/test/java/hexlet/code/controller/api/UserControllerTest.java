package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.extension.PostgresDbCleanerExtension;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ExtendWith(PostgresDbCleanerExtension.class)
class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User testUser;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();
        testUser = Instancio.create(modelGenerator.userModel());
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    @WithMockUser
    public void testCreate() throws Exception {
        var request = post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser));
        var response = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

        var body = response.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("password").isNotEqualTo(testUser.getPassword()), v -> v.node("createdAt").isNotNull());

        var user = userRepository.findByEmail(testUser.getEmail()).get();
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    @WithMockUser
    public void testDestroy() throws Exception {
        userRepository.save(testUser);

        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token)).andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    @WithMockUser
    public void testIndex() throws Exception {
        userRepository.save(testUser);

        var result = mockMvc.perform(get("/api/users").with(jwt())).andExpect(status().isOk()).andReturn();

        var body = result.getResponse().getContentAsString();
        List<UserDTO> actualDTOs = objectMapper.readValue(body, new TypeReference<>() {
        });

        List<UserDTO> expectedDTOs = userRepository.findAll().stream().map(userMapper::map).toList();

        assertThat(actualDTOs).usingRecursiveComparison().isEqualTo(expectedDTOs);
    }

    @Test
    @WithMockUser
    public void testPartialUpdate() throws Exception {
        userRepository.save(testUser);

        var updateDto = new UserUpdateDTO();
        updateDto.setFirstName(JsonNullable.of("Dmitry"));

        var request = put("/api/users/" + testUser.getId()).with(token).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        var response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        var body = response.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("firstName").isEqualTo("Dmitry"),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()));

        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getFirstName()).isEqualTo("Dmitry");
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    @WithMockUser
    public void testShow() throws Exception {
        userRepository.save(testUser);
        var response = mockMvc.perform(get("/api/users/" + testUser.getId())).andExpect(status().isOk()).andReturn();
        var body = response.getResponse().getContentAsString();

        assertThatJson(body).and(v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()));
    }

    @Test
    public void testShowUnauthorized() throws Exception {
        userRepository.save(testUser);
        mockMvc.perform(get("/api/users/" + testUser.getId())).andExpect(status().isUnauthorized());
    }
}
