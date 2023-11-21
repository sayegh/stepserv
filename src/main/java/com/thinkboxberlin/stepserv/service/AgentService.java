package com.thinkboxberlin.stepserv.service;

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

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentByUuid(final String uuid) throws NoSuchElementException {
        final Optional<Agent> agent = agentRepository.findById(uuid);
        if (agent.isEmpty())
            throw new NoSuchElementException();
        return agent.get();
    }

    public void save(final Agent agent) {
        agentRepository.save(agent);
    }
}
