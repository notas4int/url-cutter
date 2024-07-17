package org.artem.projects.effective_mobile.url_cut_app.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.artem.projects.effective_mobile.url_cut_app.dto.CreatingShortedUrlRequest;
import org.artem.projects.effective_mobile.url_cut_app.service.URLService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/super-url-cutter")
@RequiredArgsConstructor
public class URLController {
    private final URLService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestBody CreatingShortedUrlRequest request) {
        return ResponseEntity.ok(urlService.shorten(request));
    }

    @GetMapping("/{alies}")
    public void redirect(@PathVariable String alies, HttpServletResponse response) {
        String returnUrl = urlService.getOriginalUrlByAlias(alies);
        try {
            response.sendRedirect(returnUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
