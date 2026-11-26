package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends
        JpaRepository<CategoryJpaEntity, String>,
        JpaSpecificationExecutor<CategoryJpaEntity> {

    @Query(value = "select c.id from Category c where c.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
