package org.artem.projects.effective_mobile.url_cut_app.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.artem.projects.effective_mobile.url_cut_app.dto.CreatingShortedUrlRequest;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.ShortedUrlNotFoundException;
import org.artem.projects.effective_mobile.url_cut_app.models.UrlDependencies;
import org.artem.projects.effective_mobile.url_cut_app.repositories.URLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class URLServiceImpl implements URLService {
    @Value("${service.url-domain}")
    private String domainUrl;

    private final URLRepository urlRepository;

    @Transactional
    @Override
    public String shorten(CreatingShortedUrlRequest urlRequest) {
        String shortenedUrl;
        String randomAlias = null;
        if (urlRequest.alias() != null)
            shortenedUrl = "http://" + domainUrl + "/" + urlRequest.alias();
        else {
            // TODO: поиграть с проверкой уникальности
            randomAlias = RandomStringUtils.random(50, true, true);
            shortenedUrl = "http://" + domainUrl + "/" + randomAlias;
        }

        LocalDateTime timestamp =
                urlRequest.expiredAfter() != null ? LocalDateTime.now().plusSeconds(urlRequest.expiredAfter().getSecond()) : null;
        UrlDependencies urlDependencies = UrlDependencies.builder()
                .originalUrl(urlRequest.url())
                .alias(randomAlias == null ? urlRequest.alias() : randomAlias)
                .shortenedUrl(shortenedUrl)
                .expirationTime(timestamp)
                .build();

        urlRepository.save(urlDependencies);
        return shortenedUrl;
    }

    @Transactional
    @Override
    public String getOriginalUrlByAlias(String alias) {
        UrlDependencies urlDependencies = urlRepository.findByAlias(alias)
                .orElseThrow(() -> new ShortedUrlNotFoundException("Url '" + "http://" + domainUrl + alias + "' not found"));

        return urlDependencies.getOriginalUrl();
    }
}
