package com.thinkboxberlin.stepserv.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thinkboxberlin.stepserv.Application;
import com.thinkboxberlin.stepserv.security.authentication.UserRole;
import com.thinkboxberlin.stepserv.security.exception.LoginAlreadyExistsException;
import com.thinkboxberlin.stepserv.security.model.SignInDto;
import com.thinkboxberlin.stepserv.security.model.SignUpDto;
import com.thinkboxberlin.stepserv.security.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
public class AuthControllerIntegrationTest {
    private final String URL_ROOT = "/api/v1/auth";
    private final String TEST_USER_NAME_SIGNUP = "Test User";
    private final String TEST_USER_PASSWORD_SIGNUP = "Test Password";
    private final String TEST_USER_NAME_SIGNIN = "Test User SignIn";
    private final String TEST_USER_PASSWORD_SIGNIN = "Test Password SignIn";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Test
    public void shouldSignup() throws Exception {
        // Given
        final String uri = URL_ROOT + "/signup";
        final SignUpDto signUpDto = new SignUpDto(TEST_USER_NAME_SIGNUP, TEST_USER_PASSWORD_SIGNUP, UserRole.USER);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(signUpDto);

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isCreated());
    }

    @Test
    public void shouldSignIn() throws Exception {
        // Given
        try {
            authService.signUp(new SignUpDto(TEST_USER_NAME_SIGNIN, TEST_USER_PASSWORD_SIGNIN, UserRole.USER));
        } catch(LoginAlreadyExistsException ex) {
            // ignored
        }
        final String uri = URL_ROOT + "/signin";
        final SignInDto signInDto = new SignInDto(TEST_USER_NAME_SIGNIN, TEST_USER_PASSWORD_SIGNIN);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(signInDto);

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldNotSignIn() throws Exception {
        // Given
        try {
            authService.signUp(new SignUpDto(TEST_USER_NAME_SIGNIN, TEST_USER_PASSWORD_SIGNIN, UserRole.USER));
        } catch(LoginAlreadyExistsException ex) {
            // ignored
        }
        final String uri = URL_ROOT + "/signin";
        final SignInDto signInDto = new SignInDto(TEST_USER_NAME_SIGNIN, "wrong password");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(signInDto);

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isForbidden());
    }
}