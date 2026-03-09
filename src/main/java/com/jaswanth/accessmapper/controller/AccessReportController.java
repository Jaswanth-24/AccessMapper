package com.jaswanth.accessmapper.controller;

import com.jaswanth.accessmapper.DTO.AccessReportResponseDTO;
import com.jaswanth.accessmapper.DTO.RepositoryDTO;
import com.jaswanth.accessmapper.client.GitHubClient;
import com.jaswanth.accessmapper.service.AccessReportService;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccessReportController {

    private final AccessReportService service;
    private final GitHubClient gitHubClient;

    public AccessReportController(AccessReportService service, GitHubClient gitHubClient) {
        this.service = service;
        this.gitHubClient = gitHubClient;
    }

    @GetMapping("/orgs")
    public List<String> getOrganizations(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {

        if (authorizedClient == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User not authenticated. Please login with GitHub."
            );
        }


        String token = authorizedClient.getAccessToken().getTokenValue();

        return gitHubClient.getUserOrganizations(token);
    }

    @GetMapping("/access-report")
    public AccessReportResponseDTO getReport(
            @RequestParam String org,
            @RegisteredOAuth2AuthorizedClient("github")OAuth2AuthorizedClient authorizedClient) {

        if (authorizedClient == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User not authenticated. Please login with GitHub."
            );
        }

        String token = authorizedClient.getAccessToken().getTokenValue();

        return service.generateReport(org, token);
    }
    @GetMapping("/no-organization")
    public Map<String, String> noOrganization() {
        return Map.of(
                "error", "No organization found for this GitHub account"
        );
    }

    @GetMapping("/orgs/{org}/repos")
    public List<RepositoryDTO> getRepositories(
            @PathVariable String org,
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient) {

        if (authorizedClient == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User not authenticated. Please login with GitHub."
            );
        }

        String token = authorizedClient.getAccessToken().getTokenValue();

        return service.getRepositories(org, token);
    }
}