package org.artem.projects.effective_mobile.url_cut_app.dto;


import java.time.LocalDateTime;

public record CreatingShortedUrlRequest(String url, String alias, LocalDateTime expiredAfter) {
}
