package com.fullcycle.admin.catalogo.infrastructure.genre;

import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreId;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

@Component
@SuppressWarnings("null")
public class GenreMySQLGateway implements GenreGateway {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "active",
            "createdAt",
            "updatedAt",
            "deletedAt"
    );

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public void deleteById(final GenreId anId) {
        final var aGenreId = anId.getValue();
        if (this.genreRepository.existsById(aGenreId)) {
            this.genreRepository.deleteById(aGenreId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genre> findById(final GenreId anId) {
        return this.genreRepository.findWithCategoriesById(anId.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagination<Genre> findAll(final SearchQuery aQuery) {
        final var sortField = resolveSortField(aQuery.sort());
        final var sortDirection = resolveDirection(aQuery.direction());
        final var page = PageRequest.of(
                Math.max(aQuery.page(), 0),
                Math.max(aQuery.perPage(), 1),
                Sort.by(sortDirection, sortField)
        );

        final var where = buildTermsFilter(aQuery.terms());
        final var pageResult = this.genreRepository.findAll(where, page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.stream().map(GenreJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<GenreId> existsByIds(final Iterable<GenreId> genreIds) {
        final var ids = StreamSupport.stream(genreIds.spliterator(), false)
                .map(GenreId::getValue)
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return this.genreRepository.existsByIds(ids).stream()
                .map(GenreId::from)
                .toList();
    }

    private Genre save(final Genre aGenre) {
        return this.genreRepository.save(GenreJpaEntity.from(aGenre)).toAggregate();
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

    private static Specification<GenreJpaEntity> buildTermsFilter(final String terms) {
        if (terms == null || terms.isBlank()) {
            return null;
        }
        return SpecificationUtils.like("name", terms.trim());
    }
}
