package com.thinkboxberlin.stepserv.security.model;

public record SignInDto(
    String login,
    String password) {
}
