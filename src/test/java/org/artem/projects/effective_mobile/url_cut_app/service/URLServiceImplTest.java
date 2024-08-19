package org.artem.projects.effective_mobile.url_cut_app.service;

import org.artem.projects.effective_mobile.url_cut_app.dto.CreatingShortedUrlRequest;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.AliasAlreadyUsedException;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.ShortedUrlNotFoundException;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.UrlTimeExpiredLivenessException;
import org.artem.projects.effective_mobile.url_cut_app.models.UrlDependencies;
import org.artem.projects.effective_mobile.url_cut_app.repositories.URLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class URLServiceImplTest {
    @Mock
    URLRepository urlRepository;

    @InjectMocks
    URLServiceImpl urlService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(urlService, "domainUrl", "localhost:8080/api/v1/super-url-cutter");
    }

    @Test
    public void shouldReturnShortenString() {
        CreatingShortedUrlRequest request = CreatingShortedUrlRequest.builder()
                .url("https://google.com")
                .build();

        when(urlRepository.existsByAlias(any(String.class))).thenReturn(false);

        String result = urlService.shorten(request);
        assertNotNull(result);
        assertTrue(Pattern.matches("http://localhost:8080/api/v1/super-url-cutter/\\w+", result));
    }

    @Test
    public void shouldReturnShortenString_WhenRequestIncludesAlias() {
        CreatingShortedUrlRequest request = CreatingShortedUrlRequest.builder()
                .url("https://google.com")
                .alias("testAlias")
                .build();

        when(urlRepository.existsByAlias("testAlias")).thenReturn(false);

        String result = urlService.shorten(request);
        assertNotNull(result);
        assertTrue(Pattern.matches("http://localhost:8080/api/v1/super-url-cutter/" + request.alias(), result));
    }

    @Test
    public void shouldReturnShortenString_WhenRequestIncludesExpTime() {
        CreatingShortedUrlRequest request = CreatingShortedUrlRequest.builder()
                .url("https://google.com")
                .expiredAfter(LocalDateTime.now().plusDays(1))
                .build();

        when(urlRepository.existsByAlias(any(String.class))).thenReturn(false);

        String result = urlService.shorten(request);
        assertNotNull(result);
        assertTrue(Pattern.matches("http://localhost:8080/api/v1/super-url-cutter/\\w+", result));
    }

    @Test
    public void shouldReturnShortenString_WhenRequestIncludesAliasAndExpTime() {
        CreatingShortedUrlRequest request = CreatingShortedUrlRequest.builder()
                .url("https://google.com")
                .alias("testAlias")
                .expiredAfter(LocalDateTime.now().plusDays(1))
                .build();

        when(urlRepository.existsByAlias("testAlias")).thenReturn(false);

        String result = urlService.shorten(request);
        assertNotNull(result);
        assertTrue(Pattern.matches("http://localhost:8080/api/v1/super-url-cutter/" + request.alias(), result));
    }

    @Test
    public void shouldThrowAliasAlreadyUsedException_AfterRequestIncludesNotUniqueAlias() {
        CreatingShortedUrlRequest request = CreatingShortedUrlRequest.builder()
                .url("https://google.com")
                .alias("testAlias")
                .build();

        when(urlRepository.existsByAlias("testAlias")).thenReturn(true);

        assertThrows(AliasAlreadyUsedException.class, () -> urlService.shorten(request));
    }

    @Test
    public void shouldReturnOriginalUrl() {
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .originalUrl("https://google.com")
                .alias("testAlias")
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/testAlias")
                .build();

        when(urlRepository.findByAlias(urlDependencies.getAlias())).thenReturn(Optional.of(urlDependencies));

        String result = urlService.getOriginalUrlByAlias(urlDependencies.getAlias());
        assertNotNull(result);
        assertEquals("https://google.com", result);
    }

    @Test
    public void shouldThrowShortedUrlNotFoundException_WhenAliasNotFound() {
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .originalUrl("https://google.com")
                .alias("testAliasNotFound")
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/testAlias")
                .build();

        when(urlRepository.findByAlias(urlDependencies.getAlias())).thenReturn(Optional.empty());

        assertThrows(ShortedUrlNotFoundException.class,
                () -> urlService.getOriginalUrlByAlias(urlDependencies.getAlias()));
    }

    @Test
    public void shouldReturnOriginalUrl_WhenRequestIncludesExpTime() {
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .originalUrl("https://google.com")
                .alias("testAlias")
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/testAlias")
                .expirationTime(LocalDateTime.now().plusDays(1))
                .build();

        when(urlRepository.findByAlias(urlDependencies.getAlias())).thenReturn(Optional.of(urlDependencies));

        String result = urlService.getOriginalUrlByAlias(urlDependencies.getAlias());
        assertNotNull(result);
        assertEquals("https://google.com", result);
    }

    @Test
    public void shouldThrowUrlTimeExpiredLivenessException_WhenTimeExpired() {
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .originalUrl("https://google.com")
                .alias("testAlias")
                .shortenedUrl("http://localhost:8080/api/v1/super-url-cutter/testAlias")
                .expirationTime(LocalDateTime.now().minusDays(1))
                .build();

        when(urlRepository.findByAlias(urlDependencies.getAlias())).thenReturn(Optional.of(urlDependencies));

        assertThrows(UrlTimeExpiredLivenessException.class,
                () -> urlService.getOriginalUrlByAlias(urlDependencies.getAlias()));
    }
}