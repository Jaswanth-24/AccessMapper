package com.jaswanth.accessmapper.controller;

import com.jaswanth.accessmapper.DTO.AccessReportResponseDTO;
import com.jaswanth.accessmapper.DTO.RepositoryDTO;
import com.jaswanth.accessmapper.client.GitHubClient;
import com.jaswanth.accessmapper.service.AccessReportService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccessReportController {

    private final AccessReportService service;
    private final GitHubClient gitHubClient;


    public AccessReportController(AccessReportService service, GitHubClient gitHubClient) {
        this.service = service;
        this.gitHubClient = gitHubClient;
    }

    //return list of organizations
    @GetMapping("/orgs")
    public List<String> getOrganizations() {
        return gitHubClient.getUserOrganizations();
    }

    //returns Access Report of all the repositories of the organization
    @GetMapping("/access-report")
    public AccessReportResponseDTO getReport(@RequestParam String org) {
        return service.generateReport(org);
    }

    //return Repositories of the organization
    @GetMapping("/orgs/{org}/repos")
    public List<RepositoryDTO> getRepositories(@PathVariable String org) {
        return service.getRepositories(org);
    }
}