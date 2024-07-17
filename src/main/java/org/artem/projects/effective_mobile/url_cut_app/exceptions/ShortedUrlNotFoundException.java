package org.artem.projects.effective_mobile.url_cut_app.exceptions;

public class ShortedUrlNotFoundException extends RuntimeException {
    public ShortedUrlNotFoundException(String s) {
        super(s);
    }
}
