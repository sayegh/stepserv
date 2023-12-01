package com.thinkboxberlin.stepserv.rest;

import com.thinkboxberlin.stepserv.Application;
import com.thinkboxberlin.stepserv.repository.AgentRepository;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AgentRepository agentRepository;

    /* Temporarily disabled TODO - authentication based testing
     * This should be an integration test with an in-memory database.
    @ParameterizedTest
    // @ValueSource(strings = {"/all", "/get-agent-data"}) // six numbers
    @ValueSource(strings = {"/all"}) // six numbers
    public void shouldReturnOKForRequests(final String uri) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    */
}
