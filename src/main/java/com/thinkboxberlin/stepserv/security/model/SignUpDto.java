package com.thinkboxberlin.stepserv.security.model;

import com.thinkboxberlin.stepserv.security.authentication.UserRole;

public record SignUpDto(
    String login,
    String password,
    UserRole role) {
}
