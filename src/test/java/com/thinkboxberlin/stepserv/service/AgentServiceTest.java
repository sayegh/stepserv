package com.thinkboxberlin.stepserv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.repository.AgentRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        List<Agent> givenAgentList = new ArrayList<Agent>();
        givenAgentList.add(Agent.builder()
            .agentUuid("1234")
            .agentName("Tester 1")
            .lastSeen(new Date())
            .currentLocation("unknown")
            .build());

        when(agentRepository.findAll()).thenReturn(givenAgentList);

        // When
        List<Agent> resultAgentList = agentService.getAllAgents();
        // Then
        assertEquals(resultAgentList.size(), givenAgentList.size());
    }
}
