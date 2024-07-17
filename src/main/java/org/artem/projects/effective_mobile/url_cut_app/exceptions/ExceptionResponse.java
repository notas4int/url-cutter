package org.artem.projects.effective_mobile.url_cut_app.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private String requestURI;
    private String message;
    private LocalDateTime currentTime;
}
