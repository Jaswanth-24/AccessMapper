package com.jaswanth.accessmapper.controller;

import com.jaswanth.accessmapper.DTO.AccessReportResponseDTO;
import com.jaswanth.accessmapper.DTO.RepositoryDTO;
import com.jaswanth.accessmapper.service.AccessReportService;
import com.jaswanth.accessmapper.client.GitHubClient;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class AuthController {

    private final GitHubClient gitHubClient;
    private final AccessReportService accessReportService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User user, Model model) {

        model.addAttribute("username", user.getAttribute("login"));
        model.addAttribute("avatar", user.getAttribute("avatar_url"));

        return "dashboard";
    }
}