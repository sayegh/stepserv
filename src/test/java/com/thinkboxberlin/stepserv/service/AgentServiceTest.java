package com.thinkboxberlin.stepserv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.repository.AgentRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AgentServiceTest {
    @Mock
    private AgentRepository agentRepository;

    @Test
    public void shouldGetAllAgents() {
        // Given
        final AgentService agentService = new AgentService(agentRepository);
        final List<Agent> givenAgentList = new ArrayList<Agent>();
        givenAgentList.add(Agent.builder()
            .agentUuid("1234")
            .agentName("Tester 1")
            .lastSeen(new Date())
            .currentLocation("unknown")
            .build());
        givenAgentList.add(Agent.builder()
            .agentUuid("5678")
            .agentName("Tester 2")
            .lastSeen(new Date())
            .currentLocation("unknown")
            .build());
        when(agentRepository.findAll()).thenReturn(givenAgentList);

        // When
        final List<Agent> resultAgentList = agentService.getAllAgents();

        // Then
        assertEquals(resultAgentList.size(), givenAgentList.size());
    }

    @Test
    public void shouldFindOrNotFindAgentById() {
        final AgentService agentService = new AgentService(agentRepository);
        when(agentRepository.findById(anyString()))
            .thenAnswer(invocation -> {
                    Object argument = invocation.getArguments()[0];
                    if ("4711".equals(argument)) {
                        return Optional.of(Agent.builder()
                            .agentUuid("4711")
                            .agentName("Tester 1")
                            .lastSeen(new Date())
                            .currentLocation("unknown")
                            .build());
                    } else {
                        throw new NoSuchElementException();
                    }
                }
                );

        final Agent agent = agentService.getAgentByUuid("4711");
        assertEquals(agent.getAgentUuid(), "4711");
        assertThrows(NoSuchElementException.class, () -> agentService.getAgentByUuid("1234"));
    }
}
