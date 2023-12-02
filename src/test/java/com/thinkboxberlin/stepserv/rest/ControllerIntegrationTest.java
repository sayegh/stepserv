package com.thinkboxberlin.stepserv.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thinkboxberlin.stepserv.Application;
import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.security.authentication.UserRole;
import com.thinkboxberlin.stepserv.security.exception.LoginAlreadyExistsException;
import com.thinkboxberlin.stepserv.security.model.SignUpDto;
import com.thinkboxberlin.stepserv.security.model.User;
import com.thinkboxberlin.stepserv.security.service.AuthService;
import com.thinkboxberlin.stepserv.security.service.TokenProvider;
import com.thinkboxberlin.stepserv.service.AgentService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
public class ControllerIntegrationTest {
    final String TEST_LOGIN = "testuser";
    final String TEST_PASSWORD = "testpassword";
    final String TEST_AGENT_ID_1 = "1234";
    final String TEST_AGENT_NAME_1 = "Stan Laurel";
    final String TEST_AGENT_ID_2 = "5678";
    final String TEST_AGENT_NAME_2 = "Oliver Hardy";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProvider tokenService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AgentService agentService;

    private String accessToken = null;

    @BeforeEach
    public void setupAndPopulateDatabase() {
        try {
            authService.signUp(new SignUpDto(TEST_LOGIN, TEST_PASSWORD, UserRole.USER));
        } catch(LoginAlreadyExistsException ex) {
            // ignored
        }
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(TEST_LOGIN, TEST_PASSWORD);
        Authentication authUser = authenticationManager.authenticate(usernamePassword);
        accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
        log.info("Access token: " + accessToken);
        agentService.save(Agent.builder()
            .agentUuid(TEST_AGENT_ID_1)
            .agentName(TEST_AGENT_NAME_1)
            .tags(Arrays.asList("foo", "bar"))
            .lastSeen(new Date())
            .currentLocation("unknown")
            .build());
        agentService.save(Agent.builder()
            .agentUuid(TEST_AGENT_ID_2)
            .agentName(TEST_AGENT_NAME_2)
            .tags(Arrays.asList("foo", "bar"))
            .lastSeen(new Date())
            .currentLocation("unknown")
            .build());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/all"}) // six numbers
    public void shouldReturnOKForRequests(final String uri) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(TEST_AGENT_ID_1)))
                .andExpect(content().string(containsString(TEST_AGENT_ID_2)));
    }

    @Test
    public void shouldFindAgentByUuid() throws Exception {
        final String uri = "/get-agent-data?uuid=" + TEST_AGENT_ID_2;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(TEST_AGENT_NAME_2)))
            .andExpect(content().string(containsString(TEST_AGENT_ID_2)));
    }

    @Test
    public void should() throws Exception {
        // Given
        final String uri = "/create-agent";
        final String agentUuid = UUID.randomUUID().toString();
        final String agentName = "Create Agent Test";
        final Agent agent = Agent.builder()
            .agentUuid(agentUuid)
            .agentName(agentName)
            .currentLocation("Who knows")
            .tags(new ArrayList<String>())
            .build();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(agent);
        log.info(requestJson);
        // When
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString(agentUuid)));
        // Then
        assertEquals(agentService.getAgentByUuid(agentUuid).getAgentName(), agentName);
    }

}
