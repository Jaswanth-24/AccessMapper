package com.jaswanth.accessmapper.client;

import com.jaswanth.accessmapper.DTO.RepositoryDTO;
import com.jaswanth.accessmapper.DTO.CollaboratorDTO;
import com.jaswanth.accessmapper.exception.GitHubAPIException;
import com.jaswanth.accessmapper.exception.RateLimitExceededException;
import com.jaswanth.accessmapper.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GitHubClient {

    @Value("${github.api}")
    private String githubApi;

    private final RestTemplate restTemplate;

    public GitHubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // Get organizations of the authenticated user
    public List<String> getUserOrganizations(String token) {

        try {

            String url = githubApi + "/user/orgs";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map[]> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);

            Map[] orgs = response.getBody();

            List<String> organizations = new ArrayList<>();

            if (orgs != null) {
                for (Map org : orgs) {
                    organizations.add((String) org.get("login"));
                }
            }

            return organizations;

        } catch (HttpClientErrorException.Unauthorized e) {

            throw new GitHubAPIException("Invalid GitHub token or credentials");

        } catch (HttpClientErrorException.Forbidden e) {

            throw new RateLimitExceededException("GitHub API rate limit exceeded");

        } catch (RestClientException e) {

            throw new GitHubAPIException("Error while fetching user organizations");
        }
    }


    // Get repositories of an organization
    public List<RepositoryDTO> getRepositories(String org, String token) {

        List<RepositoryDTO> repositories = new ArrayList<>();
        int page = 1;

        try {

            while (true) {

                String url = githubApi + "/orgs/" + org +
                        "/repos?per_page=100&page=" + page;

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<RepositoryDTO[]> response =
                        restTemplate.exchange(url, HttpMethod.GET, entity, RepositoryDTO[].class);

                RepositoryDTO[] repos = response.getBody();

                if (repos == null || repos.length == 0) {
                    break;
                }

                repositories.addAll(Arrays.asList(repos));
                page++;
            }

            return repositories;

        } catch (HttpClientErrorException.NotFound e) {

            throw new ResourceNotFoundException("GitHub organization not found: " + org);

        } catch (HttpClientErrorException.Forbidden e) {

            throw new RateLimitExceededException("GitHub API rate limit exceeded");

        } catch (RestClientException e) {

            throw new GitHubAPIException("Error while communicating with GitHub API");
        }

    }


    // Get collaborators of a repository
    public CollaboratorDTO[] getCollaborators(String owner, String repo, String token) {

        try {

            String url = githubApi + "/repos/" + owner + "/" + repo + "/collaborators?per_page=100";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<CollaboratorDTO[]> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, CollaboratorDTO[].class);

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {

            throw new ResourceNotFoundException("Repository not found: " + repo);

        } catch (HttpClientErrorException.Forbidden e) {

            throw new RateLimitExceededException("GitHub API rate limit exceeded");

        } catch (RestClientException e) {

            throw new GitHubAPIException("Error while fetching collaborators from GitHub API");
        }
    }
}