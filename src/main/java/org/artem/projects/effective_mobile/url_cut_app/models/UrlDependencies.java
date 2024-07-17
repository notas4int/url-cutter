package org.artem.projects.effective_mobile.url_cut_app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "urldependencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlDependencies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "original_url")
    @NotNull
    private String originalUrl;

    @NotNull
    private String alias;

    @Column(name = "shortened_url", unique = true)
    @NotNull
    private String shortenedUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
}
