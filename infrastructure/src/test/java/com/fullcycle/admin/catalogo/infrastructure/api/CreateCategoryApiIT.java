package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
public class CreateCategoryApiIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidRequest_whenCallsCreateCategory_shouldPersistAndReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Assertions.assertEquals(0, categoryRepository.count());

        final var aRequest = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var result = this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        final var actualId = this.mapper.readTree(result.getResponse().getContentAsString())
                .get("id")
                .asString();

        Assertions.assertEquals("/categories/" + actualId, result.getResponse().getHeader("Location"));
        Assertions.assertEquals(1, categoryRepository.count());

        final var persistedCategory = categoryRepository.findById(actualId).orElseThrow();

        Assertions.assertEquals(expectedName, persistedCategory.getName());
        Assertions.assertEquals(expectedDescription, persistedCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, persistedCategory.isActive());
        Assertions.assertNotNull(persistedCategory.getCreatedAt());
        Assertions.assertNotNull(persistedCategory.getUpdatedAt());
        Assertions.assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void givenAValidRequestWithInactiveCategory_whenCallsCreateCategory_shouldPersistInactiveCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        Assertions.assertEquals(0, categoryRepository.count());

        final var aRequest = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var result = this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        final var actualId = this.mapper.readTree(result.getResponse().getContentAsString())
                .get("id")
                .asString();

        Assertions.assertEquals(1, categoryRepository.count());

        final var persistedCategory = categoryRepository.findById(actualId).orElseThrow();

        Assertions.assertEquals(expectedName, persistedCategory.getName());
        Assertions.assertEquals(expectedDescription, persistedCategory.getDescription());
        Assertions.assertFalse(persistedCategory.isActive());
        Assertions.assertNotNull(persistedCategory.getCreatedAt());
        Assertions.assertNotNull(persistedCategory.getUpdatedAt());
        Assertions.assertNotNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void givenAValidRequestWithNullActive_whenCallsCreateCategory_shouldDefaultToActiveTrue() throws Exception {
        final var expectedName = "Documentários";
        final var expectedDescription = "Categoria de documentários";

        Assertions.assertEquals(0, categoryRepository.count());

        final var aRequest = new CreateCategoryRequest(expectedName, expectedDescription, null);

        final var result = this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        final var actualId = this.mapper.readTree(result.getResponse().getContentAsString())
                .get("id")
                .asString();

        final var persistedCategory = categoryRepository.findById(actualId).orElseThrow();

        Assertions.assertTrue(persistedCategory.isActive());
        Assertions.assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenCallsCreateCategory_shouldReturnUnprocessableContentAndNotPersist() throws Exception {
        final var aRequest = new CreateCategoryRequest(null, "A categoria mais assistida", true);

        this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo("'name' should not be null")));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallsCreateCategory_shouldReturnUnprocessableContentAndNotPersist() throws Exception {
        final var aRequest = new CreateCategoryRequest("   ", "A categoria mais assistida", true);

        this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo("'name' should not be empty")));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallsCreateCategory_shouldReturnUnprocessableContentAndNotPersist() throws Exception {
        final var aRequest = new CreateCategoryRequest("Fi ", "A categoria mais assistida", true);

        this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo("'name' must be between 3 and 255 characters")));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidNameLengthMoreThan255_whenCallsCreateCategory_shouldReturnUnprocessableContentAndNotPersist() throws Exception {
        final var expectedName = "a".repeat(256);
        final var aRequest = new CreateCategoryRequest(expectedName, "A categoria mais assistida", true);

        this.mvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(aRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo("'name' must be between 3 and 255 characters")));

        Assertions.assertEquals(0, categoryRepository.count());
    }
}
