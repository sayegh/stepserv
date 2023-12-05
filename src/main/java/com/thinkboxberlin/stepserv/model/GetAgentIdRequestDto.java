package com.thinkboxberlin.stepserv.model;

public record GetAgentIdRequestDto(
    String username,
    String lastname) {
}