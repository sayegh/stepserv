package com.thinkboxberlin.stepserv.rest;

import com.thinkboxberlin.stepserv.model.Agent;
import com.thinkboxberlin.stepserv.service.AgentService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class Controller {
    @Autowired
    private AgentService agentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("eventName", "FIFA 2018");
        return "index";
    }

    @GetMapping("/all")
    public List<Agent> getAllAgents() {
        log.info("Returning all agents in database");
        return agentService.getAllAgents();
    }

    @GetMapping("/get-agent-data")
    public Agent getAgentData(@RequestParam(value = "uuid") final String uuid) {
        log.info("Looking for agent with ID '{}'", uuid);
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
