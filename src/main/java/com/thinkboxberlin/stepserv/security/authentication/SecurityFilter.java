package com.thinkboxberlin.stepserv.security.authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.thinkboxberlin.stepserv.security.model.User;
import com.thinkboxberlin.stepserv.security.repository.UserRepository;
import com.thinkboxberlin.stepserv.security.service.TokenProviderService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
    static public String API_KEY_HEADER = "api-key";
    @Autowired
    TokenProviderService tokenProvider;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        var token = this.recoverToken(request);
        log.info("api-key: " + request.getHeader(API_KEY_HEADER ));
        if (token != null) {
            var login = tokenProvider.validateToken(token);
            final User user = (User)userRepository.findByLogin(login);
            log.info("stored api-key: " + user.getApiKey().toString());
            if (!user.getApiKey().toString().equals(request.getHeader(API_KEY_HEADER))) {
                throw new JWTVerificationException("Missing or wrong API key");
            }
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}
