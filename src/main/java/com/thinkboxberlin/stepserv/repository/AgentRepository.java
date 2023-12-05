package com.thinkboxberlin.stepserv.repository;

import com.thinkboxberlin.stepserv.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, String> {
}
