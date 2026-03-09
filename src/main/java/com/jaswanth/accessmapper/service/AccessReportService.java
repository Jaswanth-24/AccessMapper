package com.jaswanth.accessmapper.service;

import com.jaswanth.accessmapper.client.GitHubClient;
import com.jaswanth.accessmapper.DTO.*;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@AllArgsConstructor
public class AccessReportService {

    private final GitHubClient gitHubClient;


    @Cacheable(value = "accessReports", key = "#org + '-' + #token")
    public AccessReportResponseDTO generateReport(String org) {

        List<RepositoryDTO> repos = gitHubClient.getRepositories(org);

        Map<String, List<RepositoryAccessDTO>> userRepoMap = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1,Math.min(20, repos.size())));

        List<Callable<Void>> tasks = new ArrayList<>();

        for (RepositoryDTO repo : repos) {

            tasks.add(() -> {

                CollaboratorDTO[] collaborators =
                        gitHubClient.getCollaborators(org, repo.getName());

                if (collaborators != null) {

                    for (CollaboratorDTO collaborator : collaborators) {

                        String permission = extractPermission(collaborator);

                        RepositoryAccessDTO repoAccess = new RepositoryAccessDTO();
                        repoAccess.setRepository(repo.getName());
                        repoAccess.setPermission(permission);

                        userRepoMap
                                .computeIfAbsent(collaborator.getLogin(),
                                        k -> Collections.synchronizedList(new ArrayList<>()))
                                .add(repoAccess);
                    }
                }

                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        List<UserAccessDTO> users = userRepoMap.entrySet()
                .stream()
                .map(entry -> {
                    UserAccessDTO dto = new UserAccessDTO();
                    dto.setUsername(entry.getKey());
                    dto.setRepositories(entry.getValue());
                    return dto;
                })
                .toList();

        AccessReportResponseDTO response = new AccessReportResponseDTO();
        response.setOrganization(org);
        response.setUsers(users);

        return response;
    }

    private String extractPermission(CollaboratorDTO collaborator) {
        var p = collaborator.getPermissions();

        if (p.isAdmin())    return "admin";
        if (p.isMaintain()) return "maintain";
        if (p.isPush())     return "push";
        if (p.isTriage())   return "triage";
        if (p.isPull())     return "pull";

        return "none";
    }

    public List<RepositoryDTO> getRepositories(String org) {
        return gitHubClient.getRepositories(org);
    }
}