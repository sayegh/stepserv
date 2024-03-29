package com.thinkboxberlin.stepserv.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thinkboxberlin.stepserv.Application;
import com.thinkboxberlin.stepserv.exception.IdentityVerificationFailedException;
import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.security.authentication.SecurityFilter;
import com.thinkboxberlin.stepserv.security.authentication.UserRole;
import com.thinkboxberlin.stepserv.security.exception.LoginAlreadyExistsException;
import com.thinkboxberlin.stepserv.security.model.SignUpDto;
import com.thinkboxberlin.stepserv.security.model.User;
import com.thinkboxberlin.stepserv.security.repository.UserRepository;
import com.thinkboxberlin.stepserv.security.service.AuthService;
import com.thinkboxberlin.stepserv.security.service.TokenProviderService;
import com.thinkboxberlin.stepserv.service.AgentService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
public class AgentControllerIntegrationTest {
    static final UUID API_KEY = UUID.fromString("5956d363-75ae-4957-a8e9-83bedc05c834");
    static final String URL_ROOT = "/api/v1/step";
    static final String TEST_LOGIN = "testuser";
    static final String TEST_PASSWORD = "testpassword";
    static final String TEST_AGENT_ID_1 = "c5a07167-9bbe-4944-a7b0-a9677afa134d";
    static final String TEST_AGENT_NAME_1 = "Rider Linden";
    static final String TEST_AGENT_ID_2 = "596d50cc-69f7-4c7c-a579-145ba744a64f";
    static final String TEST_AGENT_NAME_2 = "Vix Linden";
    static final String TEST_NON_EXISTENT_ID = "ABCD";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProviderService tokenService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private UserRepository userRepository;

    private String accessToken = null;
    private String apiKey = null;

    @BeforeEach
    public void setupAndPopulateDatabase() throws IdentityVerificationFailedException {
        try {
            authService.signUp(new SignUpDto(TEST_LOGIN, TEST_PASSWORD, UserRole.ADMIN));
        } catch(LoginAlreadyExistsException ex) {
            // ignored
        }
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(TEST_LOGIN, TEST_PASSWORD);
        Authentication authUser = authenticationManager.authenticate(usernamePassword);
        accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
        final UserDetails userDetails = userRepository.findByLogin(TEST_LOGIN);
        apiKey = ((User)userDetails).getApiKey().toString();
        agentService.registerAgent(Agent.builder()
            .agentUuid(TEST_AGENT_ID_1)
            .agentName(TEST_AGENT_NAME_1)
            .tags(Arrays.asList("foo", "bar"))
            .currentLocation("unknown")
            .build());
        agentService.registerAgent(Agent.builder()
            .agentUuid(TEST_AGENT_ID_2)
            .agentName(TEST_AGENT_NAME_2)
            .tags(Arrays.asList("foo", "bar"))
            .currentLocation("unknown")
            .build());
    }

    @Test
    @ValueSource(strings = {"/all"}) // six numbers
    public void shouldReturnOKForRequests() throws Exception {
        final String uri = URL_ROOT + "/all";
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(SecurityFilter.API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(TEST_AGENT_ID_1)))
                .andExpect(content().string(containsString(TEST_AGENT_ID_2)));
    }

    @Test
    public void shouldFindAgentByUuid() throws Exception {
        final String uri = URL_ROOT + "/get-agent-data?uuid=" + TEST_AGENT_ID_2;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(SecurityFilter.API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(TEST_AGENT_NAME_2)))
                .andExpect(content().string(containsString(TEST_AGENT_ID_2)));
    }

    @Test
    public void shouldNotFindAgentByUuid() throws Exception {
        final String uri = URL_ROOT + "/get-agent-data?uuid=" + TEST_NON_EXISTENT_ID;
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(SecurityFilter.API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRegisterAgentInDatabase() throws Exception {
        // Given
        final String uri = URL_ROOT + "/register-agent";
        final String agentUuid = "c1c6f01c-902d-433e-a316-33fa45c9c5d8";
        final String agentName = "Lettie Linden";
        final Agent agent = Agent.builder()
            .agentUuid(agentUuid)
            .agentName(agentName)
            .currentLocation("Who knows")
            .tags(new ArrayList<String>())
            .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        final String requestJson = objectWriter.writeValueAsString(agent);
        // When
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(SecurityFilter.API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(containsString(agentUuid)));
        // Then
        assertEquals(agentService.getAgentByUuid(agentUuid).getAgentName(), agentName);
    }

    @Test
    public void shouldRegisterResidentInDatabase() throws Exception {
        // Given
        final String uri = URL_ROOT + "/register-agent";
        final String agentUuid = "c1c6f01c-902d-433e-a316-33fa45c9c5d8";
        final String agentName = "Bogus";
        final Agent agent = Agent.builder()
            .agentUuid(agentUuid)
            .agentName(agentName)
            .currentLocation("Who knows")
            .tags(new ArrayList<String>())
            .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        final String requestJson = objectWriter.writeValueAsString(agent);
        // When
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(SecurityFilter.API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(containsString(agentUuid)));
        // Then
        assertEquals(agentService.getAgentByUuid(agentUuid).getAgentName(), agentName);
    }
}
