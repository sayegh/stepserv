package com.thinkboxberlin.stepserv.model;

public record GetAgentIdResponseDto(
    String username,
    String agent_id,
    String lastname) {
}