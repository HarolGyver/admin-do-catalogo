package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class UpdateGenreUseCaseIT {

    @Autowired
    private UpdateGenreUseCase useCase;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidCommandWithCategories_whenCallsUpdateGenre_thenShouldReturnGenreId() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aCommand = UpdateGenreCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());

        final var actualGenre = genreRepository.findWithCategoriesById(aGenre.getId().getValue()).orElseThrow();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories.size(), actualGenre.getCategoryIds().size());
        Assertions.assertTrue(actualGenre.getCategoryIds().containsAll(expectedCategories));
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidCommandWithNullCategories_whenCallsUpdateGenre_thenShouldClearCategories() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var aGenre = genreGateway.create(Genre.newGenre("acao", true).addCategory(filmes.getId()));

        final var aCommand = UpdateGenreCommand.with(
                aGenre.getId().getValue(),
                "Ação",
                true,
                null
        );

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertEquals(aGenre.getId().getValue(), actualOutput.id());

        final var actualGenre = genreRepository.findWithCategoriesById(aGenre.getId().getValue()).orElseThrow();
        Assertions.assertTrue(actualGenre.getCategoryIds().isEmpty());
    }

    @Test
    public void givenADuplicateCategoriesId_whenCallsUpdateGenre_thenShouldReturnNotificationException() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));

        final var aCommand = UpdateGenreCommand.with(
                aGenre.getId().getValue(),
                "Ação",
                true,
                List.of(filmes.getId().getValue(), filmes.getId().getValue())
        );

        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        Assertions.assertEquals(1, actualException.getErrors().size());
        Assertions.assertEquals(
                "'categories_id' should not contain duplicate ids: " + filmes.getId().getValue(),
                actualException.getErrors().get(0).message()
        );
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_thenShouldReturnNotificationException() {
        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = CategoryId.from("456");

        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some categories could not be found: 456";

        final var aCommand = UpdateGenreCommand.with(
                aGenre.getId().getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories)
        );

        final var actualException = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand)
        );

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        final var actualGenre = genreRepository.findWithCategoriesById(aGenre.getId().getValue()).orElseThrow();
        Assertions.assertEquals("acao", actualGenre.getName());
        Assertions.assertTrue(actualGenre.getCategoryIds().isEmpty());
    }

    private List<String> asString(final List<CategoryId> ids) {
        return ids.stream()
                .map(id -> id.getValue())
                .toList();
    }
}
