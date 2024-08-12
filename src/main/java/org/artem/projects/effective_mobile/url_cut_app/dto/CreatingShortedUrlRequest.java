package org.artem.projects.effective_mobile.url_cut_app.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreatingShortedUrlRequest(String url, String alias, LocalDateTime expiredAfter) {
}
