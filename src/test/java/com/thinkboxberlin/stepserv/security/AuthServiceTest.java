package com.thinkboxberlin.stepserv.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thinkboxberlin.stepserv.security.authentication.UserRole;
import com.thinkboxberlin.stepserv.security.exception.LoginAlreadyExistsException;
import com.thinkboxberlin.stepserv.security.model.SignUpDto;
import com.thinkboxberlin.stepserv.security.model.User;
import com.thinkboxberlin.stepserv.security.repository.UserRepository;
import com.thinkboxberlin.stepserv.security.service.AuthService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
public class AuthServiceTest {
    final String TEST_LOGIN = "testuser";
    final String TEST_PASSWORD = "testpassword";

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthService authService;

    @Test
    @SneakyThrows
    public void shouldSaveNewUser() {
        UserRole userRole = UserRole.USER;
        authService.signUp(new SignUpDto(TEST_LOGIN, TEST_PASSWORD, UserRole.USER));
        verify(userRepository, times(1)).save(any());
        assertEquals(userRole.getValue(), "user");
    }

    @Test
    public void shouldReportUserExists() {
        when(userRepository.findByLogin(anyString()))
            .thenReturn(new User(TEST_LOGIN, TEST_PASSWORD, UserRole.USER));

        assertThrows(LoginAlreadyExistsException.class,
            () -> authService.signUp(new SignUpDto(TEST_LOGIN, TEST_PASSWORD, UserRole.USER)));
    }

    @Test
    public void shouldFindUserDetailsByLogin() {
        when(userRepository.findByLogin(TEST_LOGIN))
            .thenReturn(new User(TEST_LOGIN, TEST_PASSWORD, UserRole.USER));
        UserDetails userDetails = authService.loadUserByUsername(TEST_LOGIN);
        assertEquals(userDetails.getPassword(), TEST_PASSWORD);
    }
}
