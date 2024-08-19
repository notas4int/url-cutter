package org.artem.projects.effective_mobile.url_cut_app.controllers;

import org.artem.projects.effective_mobile.url_cut_app.exceptions.UrlTimeExpiredLivenessException;
import org.artem.projects.effective_mobile.url_cut_app.models.UrlDependencies;
import org.artem.projects.effective_mobile.url_cut_app.repositories.URLRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class URLControllerTest {
    @Container
    @ServiceConnection
    public final static PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:15.6-alpine")
                .withDatabaseName("url-dependencies")
                .withUsername("postgres")
                .withPassword("postgres");
        postgresContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    URLRepository repository;

    @Test
    @DisplayName("Test redirect without livetime check")
    public void redirectTest_WithoutLivetime() throws Exception {
        String alies = "super-ya-ru";
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .alias(alies)
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/super-ya-ru")
                .originalUrl("https://ya.ru/")
                .build();

        repository.save(urlDependencies);

        mockMvc.perform(get("/api/v1/super-url-cutter/{alies}", alies))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://ya.ru/"))
                .andDo(print());
    }

    @Test
    @DisplayName("Test shorten")
    public void shortenTest_WithoutLivetime() throws Exception {
        String resShortenUrl = "http://localhost:8080/api/v1/super-url-cutter/super-google";

        String jsonRequest = """
                {
                	"url": "https://www.google.com/",
                	"alias": "super-google",
                	"expiredAfter": null
                }""";

        var request = post("/api/v1/super-url-cutter/shorten")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        result -> assertEquals(resShortenUrl, result.getResponse().getContentAsString()))
                .andDo(print());
    }

    @Test
    @DisplayName("Test shorten with livetime")
    public void shortenTest() throws Exception {
        String resShortenUrl = "http://localhost:8080/api/v1/super-url-cutter/super-bing";

        LocalDateTime expiredAfter = LocalDateTime.now().plusSeconds(5);
        String jsonRequest = """
                {
                	"url": "https://www.bing.com/",
                	"alias": "super-bing",
                	"expiredAfter": "%s"
                }""".formatted(expiredAfter);

        var request = post("/api/v1/super-url-cutter/shorten")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        result -> assertEquals(resShortenUrl, result.getResponse().getContentAsString()))
                .andDo(print());
    }

    @Test
    @DisplayName("Test redirect with livetime check")
    public void redirectTest_WhenExpiredLivetime() throws Exception {
        String alies = "rambler-ru";
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .alias(alies)
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/" + alies)
                .originalUrl("https://www.rambler.ru/")
                .expirationTime(LocalDateTime.now().minusSeconds(5))
                .build();

        repository.save(urlDependencies);

        String requestUrl = "/api/v1/super-url-cutter/" + alies;
        mockMvc.perform(get(requestUrl)).andExpectAll(
                result -> assertInstanceOf(UrlTimeExpiredLivenessException.class, result.getResolvedException()),
                status().isBadRequest(),
                jsonPath("$.requestURI").value(requestUrl),
                jsonPath("$.message").value("Url '" + "http://localhost:8080/api/v1/super-url-cutter/rambler-ru" + "' expired"),
                jsonPath("$.currentTime").exists());
    }
}