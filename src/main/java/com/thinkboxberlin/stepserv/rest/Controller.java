package com.thinkboxberlin.stepserv.rest;

import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.service.AgentService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/step")
@Slf4j
public class Controller {
    @Autowired
    private AgentService agentService;

    @GetMapping("/all")
    public List<Agent> getAllAgents() {
        return agentService.getAllAgents();
    }

    @GetMapping("/get-agent-data")
    public Agent getAgentData(@RequestParam(value = "uuid") final String uuid) {
        try {
            return agentService.getAgentByUuid(uuid);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Agent " + uuid + " not found"
            );
        }
    }

    @PutMapping("/create-agent")
    public ResponseEntity<String> createAgent(@RequestBody Agent agentDetails)  {
        agentService.save(agentDetails);
        return ResponseEntity.ok(agentDetails.getAgentUuid());
    }}
