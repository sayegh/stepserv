package com.thinkboxberlin.stepserv.service;

import com.thinkboxberlin.stepserv.exception.IdentityVerificationFailedException;
import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.repository.AgentRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AgentService {
    @Autowired
    final AgentRepository agentRepository;

    @Autowired
    final IdentityVerificationService identityVerificationService;

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentByUuid(final String uuid) throws NoSuchElementException {
        final Optional<Agent> agent = agentRepository.findById(uuid);
        if (agent.isEmpty())
            throw new NoSuchElementException();
        return agent.get();
    }

    public void registerAgent(final Agent agent) throws IdentityVerificationFailedException {
        final String[] segments = agent.getAgentName().split(" ");
        final String username = segments[0];
        final String lastname = (segments.length == 2 ? segments[1] : "Resident");

        identityVerificationService.verifyIdentity(username, lastname, agent.getAgentUuid());
        agentRepository.save(agent);
    }
}
