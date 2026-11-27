# GatoFlix — Admin do Catálogo

Microsserviço backend responsável pela administração do catálogo de vídeos da plataforma GatoFlix, construído em Java com **Clean Architecture** e **Domain-Driven Design (DDD)**.

## Arquitetura do Projeto

O projeto é organizado como um multi-módulo Gradle com separação estrita de responsabilidades:

```text
admin-do-catalogo-gatoflix/
├── domain/          # Entidades, agregados, objetos de valor, validações, exceções e gateways (zero dependência de frameworks)
├── application/     # Casos de uso (UseCases), comandos, outputs e orquestração de regras de negócio
└── infrastructure/  # Spring Boot, JPA/Hibernate, Flyway, MySQL, Controllers REST, OpenAPI e testes de integração/E2E
```

## Tecnologias

- **Java 27** + **Gradle** (Wrapper)
- **Spring Boot 4.2.0** (`webmvc`, `data-jpa`, `flyway`)
- **MySQL** (ambiente de desenvolvimento/produção e testes E2E com **Testcontainers**)
- **H2 Database** em modo MySQL (testes de slice/integração)
- **Flyway** para versionamento de migrations SQL (`infrastructure/src/main/resources/db/migration`)
- **Springdoc OpenAPI 3** (Swagger UI)
- **Vavr**, **JUnit 5** e **Mockito**

## Pré-requisitos

- **Java 27** (JDK)
- **Docker** e **Docker Compose** (para subir o MySQL local e executar testes E2E com Testcontainers)

## Como Executar Localmente

1. **Suba o banco de dados MySQL:**

   ```bash
   docker compose up -d
   ```

2. **Inicie a aplicação com o profile `development`:**

   ```bash
   ./gradlew :infrastructure:bootRun --args='--spring.profiles.active=development'
   ```

   As migrations do Flyway são aplicadas automaticamente na inicialização da aplicação.

3. **Acesse a API e a documentação Swagger:**
   - Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/api/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

### Variáveis de Ambiente Principais

| Variável | Descrição | Valor padrão (`development`) |
| --- | --- | --- |
| `SERVER_PORT` | Porta HTTP da aplicação | `8080` |
| `MYSQL_URL` | URL JDBC do MySQL | `jdbc:mysql://localhost:3306/adm_videos?...` |
| `MYSQL_USERNAME` | Usuário do banco | `root` |
| `MYSQL_PASSWORD` | Senha do banco | `123456` |
| `HIKARI_MAX_POOL_SIZE` | Tamanho máximo do pool HikariCP | `20` |

## Endpoints Principais

### Categorias (`/api/categories`)

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/categories` | Cria uma nova categoria |
| `GET` | `/api/categories` | Lista categorias paginadas (`search`, `page`, `perPage`, `sort`, `dir`) |
| `GET` | `/api/categories/{id}` | Busca uma categoria por ID |
| `PUT` | `/api/categories/{id}` | Atualiza uma categoria por ID |
| `DELETE` | `/api/categories/{id}` | Remove uma categoria por ID |

### Gêneros (`/api/genres`)

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/genres` | Cria um novo gênero |
| `GET` | `/api/genres` | Lista gêneros paginados (`search`, `page`, `perPage`, `sort`, `dir`) |
| `GET` | `/api/genres/{id}` | Busca um gênero por ID |
| `PUT` | `/api/genres/{id}` | Atualiza um gênero por ID |
| `DELETE` | `/api/genres/{id}` | Remove um gênero por ID |

## Executando os Testes

- **Todos os módulos:**

  ```bash
  ./gradlew test
  ```

- **Por módulo:**

  ```bash
  ./gradlew :domain:test
  ./gradlew :application:test
  ./gradlew :infrastructure:test
  ```

## Build do Artefato Executável

Para gerar o JAR executável (`build/libs/application.jar`):

```bash
./gradlew :infrastructure:bootJar
```
