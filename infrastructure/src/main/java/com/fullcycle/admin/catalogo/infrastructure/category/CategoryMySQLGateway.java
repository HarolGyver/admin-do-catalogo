package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils.like;

@Component
@SuppressWarnings("null")
public class CategoryMySQLGateway implements CategoryGateway {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "description",
            "active",
            "createdAt",
            "updatedAt",
            "deletedAt");

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Category create(final Category category) {
        return persist(category);
    }

    @Override
    public void deleteById(final CategoryId id) {
        final var rawId = id.getValue();
        if (this.repository.existsById(rawId)) {
            this.repository.deleteById(rawId);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryId id) {
        return this.repository.findById(id.getValue())
                .map(entity -> entity.toAggregate());
    }

    @Override
    public Category update(final Category category) {
        return persist(category);
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery query) {
        final var sortField = resolveSortField(query.sort());
        final var sortDirection = resolveDirection(query.direction());
        final var pageRequest = PageRequest.of(
                Math.max(query.page(), 0),
                Math.max(query.perPage(), 1),
                Sort.by(sortDirection, sortField));

        final var filter = buildTermsFilter(query.terms());
        final var pageResult = this.repository.findAll(filter, pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.stream().map(CategoryJpaEntity::toAggregate).toList());
    }

    @Override
    public List<CategoryId> existsByIds(final Iterable<CategoryId> categoryIds) {
        final var ids = StreamSupport.stream(categoryIds.spliterator(), false)
                .map(CategoryId::getValue)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return this.repository.existsByIds(ids).stream()
                .map(CategoryId::from)
                .toList();
    }

    private Category persist(final Category category) {
        return this.repository.save(CategoryJpaEntity.from(category)).toAggregate();
    }

    private static String resolveSortField(final String sort) {
        if (sort != null && ALLOWED_SORT_FIELDS.contains(sort)) {
            return sort;
        }
        return "name";
    }

    private static Direction resolveDirection(final String direction) {
        return Direction.fromOptionalString(direction).orElse(Direction.ASC);
    }

    private static Specification<CategoryJpaEntity> buildTermsFilter(final String terms) {
        if (terms == null || terms.isBlank()) {
            return null;
        }
        final var normalizedTerms = terms.trim();
        return SpecificationUtils.<CategoryJpaEntity>like("name", normalizedTerms)
                .or(like("description", normalizedTerms));
    }
}
