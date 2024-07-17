package org.artem.projects.effective_mobile.url_cut_app.repositories;

import org.artem.projects.effective_mobile.url_cut_app.models.UrlDependencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<UrlDependencies, Long> {
    Optional<UrlDependencies> findByAlias(String alias);
}
