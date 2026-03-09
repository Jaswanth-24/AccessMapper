package com.jaswanth.accessmapper.controller;

import com.jaswanth.accessmapper.DTO.AccessReportResponseDTO;
import com.jaswanth.accessmapper.DTO.RepositoryDTO;
import com.jaswanth.accessmapper.client.GitHubClient;
import com.jaswanth.accessmapper.service.AccessReportService;

import org.springframework.beans.factory.annotation.Value;
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
    public List<String> getOrganizations() {
        return gitHubClient.getUserOrganizations();
    }

    @GetMapping("/access-report")
    public AccessReportResponseDTO getReport(@RequestParam String org) {
        return service.generateReport(org);
    }

    @GetMapping("/no-organization")
    public Map<String, String> noOrganization() {
        return Map.of(
                "error", "No organization found for this GitHub account"
        );
    }

    @GetMapping("/orgs/{org}/repos")
    public List<RepositoryDTO> getRepositories(@PathVariable String org) {
        return service.getRepositories(org);
    }
}