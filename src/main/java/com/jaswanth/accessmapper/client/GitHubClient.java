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

    @Value("${github.token}")
    private String token;

    @Value("${github.api}")
    private String githubApi;

    private final RestTemplate restTemplate;
    public GitHubClient(RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }

    // Get repositories of an organization
    public List<RepositoryDTO> getRepositories(String org) {

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

            throw new ResourceNotFoundException("GitHub user or organization not found");

        } catch (HttpClientErrorException.Forbidden e) {

            throw new RateLimitExceededException("GitHub API rate limit exceeded");

        } catch (RestClientException e) {

            throw new GitHubAPIException("Error while communicating with GitHub API");
        }
    }

    // Get collaborators of a repository
    public CollaboratorDTO[] getCollaborators(String owner, String repo) {

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

            throw new GitHubAPIException("Error in fetching collaborators from GitHub API");
        }
    }
    public List<String> getUserOrganizations() {

        try {

            String url = githubApi + "/user/orgs";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map[]> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);

            Map[] orgs = response.getBody();

            if (orgs == null || orgs.length == 0) {
                return List.of();
            }

            List<String> organizations = new ArrayList<>();

            for (Map org : orgs) {
                organizations.add((String) org.get("login"));
            }

            return organizations;

        } catch (HttpClientErrorException.NotFound e) {

            throw new ResourceNotFoundException("GitHub user organizations not found");

        } catch (HttpClientErrorException.Forbidden e) {

            throw new RateLimitExceededException("GitHub API rate limit exceeded");

        } catch (RestClientException e) {

            throw new GitHubAPIException("Error while fetching organizations from GitHub API");
        }
    }
}