package org.artem.projects.effective_mobile.url_cut_app.exceptions;

public class AliasAlreadyUsedException extends RuntimeException {
    public AliasAlreadyUsedException(String message) {
        super(message);
    }
}
