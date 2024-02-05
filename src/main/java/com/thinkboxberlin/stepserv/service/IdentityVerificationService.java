package com.thinkboxberlin.stepserv.service;

import com.thinkboxberlin.stepserv.exception.IdentityVerificationFailedException;
import com.thinkboxberlin.stepserv.model.GetAgentIdRequestDto;
import com.thinkboxberlin.stepserv.model.GetAgentIdResponseDto;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class IdentityVerificationService {
    @Value("${second-life.api-base-url}")
    private String SECOND_LIFE_API_BASE_URL;
    @Value("${second-life.api-key}")
    private String API_KEY;

    private final String SECOND_LIFE_VERIFICATION_URI = "/get_agent_id";

    public void verifyIdentity(final String username, final String lastname, final String requestedUuid) throws
        IdentityVerificationFailedException {
        verifyIdentity(SECOND_LIFE_API_BASE_URL, username, lastname, requestedUuid);
    }

    public void verifyIdentity(final String baseUrl, final String username, final String lastname,
                               final @NotNull String requestedUuid) throws IdentityVerificationFailedException {
        // For testing on non-connected platform (e.g. Github) the test configuration doesn't require a
        // real validation, hence this method is skipped.
        if (baseUrl.length() == 0) {
            return ;
        }

        final WebClient client = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultCookie("api-key", API_KEY)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        try {
            final ResponseEntity<GetAgentIdResponseDto> response = client
                .post()
                .uri(SECOND_LIFE_VERIFICATION_URI)
                .header("api-key", API_KEY)
                .body(Mono.just(new GetAgentIdRequestDto(username, lastname)),
                    GetAgentIdRequestDto.class)
                .retrieve()
                .toEntity(GetAgentIdResponseDto.class)
                .block();

            // When arrived at this point, the HTTP status is considered 200 OK, otherwise
            // a WebClientException would have been thrown.
            if (!requestedUuid.equals(response.getBody().agent_id())) {
                throw new IdentityVerificationFailedException();
            }
        } catch(final WebClientException ex) {
            throw new IdentityVerificationFailedException();
        }
    }
}
