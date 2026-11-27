package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.application.genre.GenreCategoriesValidator;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final GenreCategoriesValidator categoriesValidator;
    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(
            final CategoryGateway categoryGateway,
            final GenreGateway genreGateway
    ) {
        this.categoriesValidator = new GenreCategoriesValidator(Objects.requireNonNull(categoryGateway));
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand aCommand) {
        final var aName = aCommand.name();
        final var isActive = aCommand.isActive();
        final var rawCategories = aCommand.categories();

        final var notification = Notification.create();
        notification.append(this.categoriesValidator.validate(rawCategories));

        final var aGenre = notification.validate(() -> Genre.newGenre(aName, isActive));

        if (notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Genre", notification);
        }

        aGenre.addCategories(this.categoriesValidator.toCategoryIds(rawCategories));

        return CreateGenreOutput.from(this.genreGateway.create(aGenre));
    }
}
