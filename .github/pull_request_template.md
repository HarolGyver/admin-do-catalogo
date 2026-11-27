# Pull Request

## Descrição

<!-- Resuma de forma clara e objetiva o que foi feito neste Pull Request e qual problema ou demanda ele resolve. -->

## Tipo de mudança

- [ ] `feat`: Nova funcionalidade
- [ ] `fix`: Correção de bug
- [ ] `refactor`: Refatoração de código (sem alterar comportamento externo)
- [ ] `test`: Adição ou ajuste de testes (unitários, integração ou E2E)
- [ ] `docs`: Atualização de documentação (README, OpenAPI, comentários)
- [ ] `chore`: Manutenção, build, dependências ou configurações (Gradle, Docker, CI)

## Camadas / Módulos afetados

- [ ] `domain`
- [ ] `application`
- [ ] `infrastructure` (API / Persistence / Config / Flyway)

## O que foi alterado

<!-- Liste os principais pontos alterados ou novos endpoints/migrations adicionados. -->
-

## Como testar

<!-- Descreva os passos ou comandos para validar este PR localmente. -->
1. Subir dependências locais (se necessário): `docker compose up -d`
2. Executar a suíte de testes: `./gradlew test`
3. Validar comportamento esperado: ...

## Checklist

- [ ] Segui os padrões de arquitetura (Clean Architecture / DDD) e convenções do projeto
- [ ] Adicionei/atualizei testes correspondentes (`domain`, `application`, `infrastructure` / E2E)
- [ ] Todos os testes passaram localmente (`./gradlew test`)
- [ ] Atualizei a documentação (`README.md` / OpenAPI) quando aplicável
- [ ] Não há arquivos temporários, credenciais ou artefatos compilados (`.class`, `build/`) no commit
- [ ] Caso haja alteração de banco de dados, criei a migration Flyway correspondente

## Referências / Issues relacionadas

<!-- Ex.: Closes #12, Refs #8 -->
