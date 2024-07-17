package org.artem.projects.effective_mobile.url_cut_app.service;

import org.artem.projects.effective_mobile.url_cut_app.dto.CreatingShortedUrlRequest;

public interface URLService {
    String shorten(CreatingShortedUrlRequest url);
    String getOriginalUrlByAlias(String url);
}
