package com.core.api.menu.controllers;

import com.core.api.menu.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/test/upstream")
public class UpstreamController {

    @Autowired
    MinioService minioService;

    @GetMapping("/get")
    @PreAuthorize("hasRole('ROLE_upstream')")
    public String getCallToUpstream() {

        try {
            minioService.getJsonFromFile("menus.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "Success: getCallToUpstream";
    }

}
