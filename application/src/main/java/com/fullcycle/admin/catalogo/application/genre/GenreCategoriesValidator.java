package com.fullcycle.admin.catalogo.application.genre;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GenreCategoriesValidator {

    private final CategoryGateway categoryGateway;

    public GenreCategoriesValidator(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    public ValidationHandler validate(final List<String> rawCategoryIds) {
        final var notification = Notification.create();
        if (rawCategoryIds == null || rawCategoryIds.isEmpty()) {
            return notification;
        }

        final var seen = new LinkedHashSet<String>();
        final var duplicates = new LinkedHashSet<String>();
        var hasNullOrBlank = false;

        for (final var rawId : rawCategoryIds) {
            if (rawId == null || rawId.isBlank()) {
                hasNullOrBlank = true;
                continue;
            }
            if (!seen.add(rawId)) {
                duplicates.add(rawId);
            }
        }

        if (hasNullOrBlank) {
            notification.append(new Error("'categories_id' should not contain null or empty values"));
        }

        if (!duplicates.isEmpty()) {
            final var duplicateIdsMessage = String.join(", ", duplicates);
            notification.append(new Error("'categories_id' should not contain duplicate ids: %s".formatted(duplicateIdsMessage)));
        }

        if (seen.isEmpty()) {
            return notification;
        }

        final var distinctIds = seen.stream()
                .map(CategoryId::from)
                .toList();

        final var retrievedIds = this.categoryGateway.existsByIds(distinctIds);

        if (distinctIds.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(distinctIds);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                    .map(CategoryId::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error("Some categories could not be found: %s".formatted(missingIdsMessage)));
        }

        return notification;
    }

    public List<CategoryId> toCategoryIds(final List<String> rawCategoryIds) {
        if (rawCategoryIds == null || rawCategoryIds.isEmpty()) {
            return List.of();
        }
        return rawCategoryIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .map(CategoryId::from)
                .toList();
    }
}
