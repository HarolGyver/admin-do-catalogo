package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends
        JpaRepository<GenreJpaEntity, String>,
        JpaSpecificationExecutor<GenreJpaEntity> {

    @EntityGraph(attributePaths = "categories")
    Optional<GenreJpaEntity> findWithCategoriesById(String id);

    @Query(value = "select g.id from Genre g where g.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
