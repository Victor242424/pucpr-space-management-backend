# 🏫 Sistema de Gerenciamento de Espaços

[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

> API REST para gerenciar espaços educacionais, controle de acesso de estudantes e relatórios de ocupação

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Recursos](#-recursos)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Executando a Aplicação](#-executando-a-aplicação)
- [Documentação da API](#-documentação-da-api)
- [Testes](#-testes)
- [Monitoramento](#-monitoramento)
- [Segurança](#-segurança)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [Esquema do Banco de Dados](#-esquema-do-banco-de-dados)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)

## 🎯 Visão Geral

O **Sistema de Gerenciamento de Espaços** é uma API REST abrangente projetada para gerenciar instalações educacionais, rastrear o acesso de estudantes a diferentes espaços (salas de aula, laboratórios, salas de estudo) e gerar relatórios detalhados de ocupação.

### Capacidades Principais

- 👥 **Gerenciamento de Estudantes**: Operações CRUD completas para registros de estudantes
- 🏢 **Gerenciamento de Espaços**: Gerenciar diferentes tipos de espaços educacionais
- 🚪 **Controle de Acesso**: Rastrear entrada e saída de estudantes em tempo real
- 📊 **Análises**: Gerar relatórios de ocupação e estatísticas de uso
- 🔐 **Segurança**: Autenticação baseada em JWT e autorização baseada em funções
- 📈 **Monitoramento**: Métricas integradas com Prometheus e endpoints do actuator
- 📚 **Documentação**: Documentação interativa da API com Swagger UI

## ✨ Recursos

### Recursos Principais

- **Autenticação Multi-Função**
    - Acesso de estudante com permissões limitadas
    - Acesso de administrador com capacidades completas de gerenciamento
    - Autenticação baseada em token JWT

- **Rastreamento de Espaços em Tempo Real**
    - Cálculo automático de ocupação
    - Validação de capacidade máxima
    - Monitoramento de acesso ativo

- **Relatórios Abrangentes**
    - Estatísticas diárias, semanais e mensais
    - Taxas e tendências de ocupação
    - Duração média de visita
    - Métricas de utilização de espaço

- **Integridade de Dados**
    - Exclusão suave para registros com dependências
    - Validação de regras de negócio
    - Gerenciamento de transações

### Recursos Técnicos

- Design de API RESTful
- Especificação OpenAPI 3.0
- Configuração baseada em perfis (dev, test, prod)
- Respostas internacionalizadas
- Tratamento de exceções
- Cobertura de código com JaCoCo
- Análise de qualidade de código com SonarQube

## 🗂️ Arquitetura

### Arquitetura em Camadas

```
┌─────────────────────────────────────┐
│         Camada de Apresentação      │
│    (Controllers + DTOs)             │
├─────────────────────────────────────┤
│         Camada de Negócio           │
│         (Services)                  │
├─────────────────────────────────────┤
│         Camada de Persistência      │
│    (Repositories + Entities)        │
├─────────────────────────────────────┤
│         Camada de Banco de Dados    │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

### Padrões de Design

- **Padrão Repository**: Abstração de acesso a dados
- **Padrão DTO**: Transferência de dados entre camadas
- **Camada de Serviço**: Encapsulamento de lógica de negócio
- **Injeção de Dependência**: Inversão de controle
- **Padrão Builder**: Construção de objetos

## 🛠️ Tecnologias

### Backend

- **Java 21**: Versão LTS mais recente
- **Spring Boot 3.2.0**: Framework de aplicação
- **Spring Data JPA**: Persistência de dados
- **Spring Security**: Autenticação e Autorização
- **PostgreSQL**: Banco de dados de produção
- **H2**: Banco de dados em memória para testes

### Segurança

- **JWT (jjwt 0.12.3)**: Autenticação baseada em token
- **BCrypt**: Hash de senhas
- **Spring Security**: Framework de segurança

### Documentação

- **SpringDoc OpenAPI 3 (2.3.0)**: Documentação da API
- **Swagger UI**: Testes interativos da API

### Monitoramento e Métricas

- **Spring Actuator**: Monitoramento da aplicação
- **Micrometer**: Coleta de métricas
- **Prometheus**: Armazenamento e consulta de métricas

### Testes

- **JUnit 5**: Framework de testes unitários
- **Mockito**: Framework de simulação
- **AssertJ**: Asserções fluentes
- **Spring Security Test**: Testes de segurança

### Qualidade de Código

- **JaCoCo**: Cobertura de código
- **SonarQube**: Análise de qualidade de código
- **Lombok**: Redução de código boilerplate

### Build e Desenvolvimento

- **Maven**: Automação de build
- **Git**: Controle de versão

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter o seguinte instalado:

- **Java Development Kit (JDK) 21** ou superior
  ```bash
  java -version
  # Deve exibir: openjdk version "21" ou superior
  ```

- **Maven 3.8+**
  ```bash
  mvn -version
  # Deve exibir: Apache Maven 3.8.x ou superior
  ```

- **PostgreSQL 14+** (para produção)
  ```bash
  psql --version
  # Deve exibir: psql (PostgreSQL) 14.x ou superior
  ```

- **Git**
  ```bash
  git --version
  ```

## 🚀 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/victor-rivas-dev/space-management.git
cd space-management
```

### 2. Configuração do Banco de Dados

#### Para Desenvolvimento (PostgreSQL)

```bash
# Criar banco de dados
createdb education_spaces_db

# Ou usando psql
psql -U postgres
CREATE DATABASE education_spaces_db;
\q
```

#### Para Testes (H2)

Nenhuma configuração necessária - H2 é executado em memória automaticamente durante os testes.

### 3. Instalar Dependências

```bash
mvn clean install
```

## ⚙️ Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` no diretório raiz (para produção):

```bash
# Configuração do Banco de Dados
DATABASE_URL=jdbc:postgresql://localhost:5432/education_spaces_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha

# Configuração JWT
JWT_SECRET=sua_chave_secreta_jwt_muito_segura_com_pelo_menos_32_caracteres
JWT_EXPIRATION=3600000

# Configuração CORS
CORS_ALLOWED_ORIGIN_1=https://seudominio.com
CORS_ALLOWED_ORIGIN_2=https://www.seudominio.com

# Configuração do Servidor
SERVER_PORT=8080

# Configuração Swagger (opcional)
SWAGGER_ENABLED=false
```

### Perfis da Aplicação

A aplicação suporta três perfis:

#### Perfil de Desenvolvimento (`dev`)

**Arquivo**: `src/main/resources/application-dev.yaml`

- Usa banco de dados PostgreSQL
- Swagger UI habilitado
- Log detalhado
- CORS permissivo para desenvolvimento local
- Token JWT válido por 24 horas

```bash
# Ativar perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Perfil de Teste (`test`)

**Arquivo**: `src/main/resources/application-test.yaml`

- Usa banco de dados H2 em memória
- Swagger desabilitado
- Ativado automaticamente durante os testes
- CORS totalmente aberto para testes

```bash
# Executar testes
mvn test
```

#### Perfil de Produção (`prod`)

**Arquivo**: `src/main/resources/application-prod.yaml`

- Usa PostgreSQL com variáveis de ambiente
- Swagger desabilitado por padrão
- Log mínimo
- CORS restrito
- Token JWT válido por 1 hora
- Pool de conexões otimizado

```bash
# Executar em produção
export SPRING_PROFILES_ACTIVE=prod
java -jar target/space-management-0.0.1-SNAPSHOT.jar
```

## 🏃 Executando a Aplicação

### Modo de Desenvolvimento

```bash
# Usando Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou
mvn clean package
java -jar target/space-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

A aplicação iniciará em `http://localhost:8081`

### Modo de Produção

```bash
# Definir variáveis de ambiente
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=sua_chave_secreta
export DATABASE_PASSWORD=sua_senha_bd

# Executar aplicação
java -jar target/space-management-0.0.1-SNAPSHOT.jar
```

# 🐳 Implantação com Docker

## Início Rápido com Docker Compose

A maneira mais fácil de executar a stack completa (Aplicação + PostgreSQL + Monitoramento):

### 1. Iniciar Todos os Serviços

```bash
# Iniciar todos os serviços em modo destacado
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Verificar status dos serviços
docker-compose ps
```
**Nota**: Certifique-se de que a rede `space-network` existe antes de executar:
```bash
docker network create space-network
```

Os seguintes serviços estarão disponíveis:

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **Aplicação** | http://localhost:8081 | - |
| **Swagger UI** | http://localhost:8081/swagger-ui.html | - |
| **PostgreSQL** | localhost:5432 | postgres/postgres |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **SonarQube** | http://localhost:9000 | admin/admin |

### 2. Parar Todos os Serviços

```bash
# Parar serviços
docker-compose down

# Parar e remover volumes (⚠️ deleta todos os dados)
docker-compose down -v
```

### 3. Reconstruir Após Mudanças no Código

```bash
# Reconstruir apenas a aplicação
docker-compose up -d --build app

# Reconstruir tudo
docker-compose up -d --build
```

---

## Docker Standalone (Sem Docker Compose)

Se preferir executar apenas o contêiner da aplicação:

### Passo 1: Construir o JAR

```bash
mvn clean package -DskipTests
```

### Passo 2: Construir Imagem Docker

```bash
docker build -t space-management:latest .
```

### Passo 3: Executar Contêiner

#### Opção A: Modo de Desenvolvimento (com PostgreSQL externo)

```bash
docker run -d \
  --name space-management \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/education_spaces_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=sua_senha \
  -e JWT_SECRET=minhaChaveSecretaParaSistemaDeGerenciamentoDeEspacosEducacionaisQueSejaSuficientementeLongaParaDesenvolvimento12345 \
  space-management:latest
```

#### Opção B: Modo de Produção

```bash
docker run -d \
  --name space-management \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://seu-host-bd:5432/education_spaces_db \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=sua_senha_segura \
  -e JWT_SECRET=sua_chave_secreta_jwt_muito_segura_com_pelo_menos_32_caracteres \
  -e JWT_EXPIRATION=3600000 \
  space-management:latest
```

### Passo 4: Verificar se o Contêiner está Executando

```bash
# Verificar status do contêiner
docker ps

# Ver logs
docker logs -f space-management

# Verificar saúde
curl http://localhost:8081/actuator/health
```

### Passo 5: Parar e Remover Contêiner

```bash
# Parar contêiner
docker stop space-management

# Remover contêiner
docker rm space-management
```

---

## Referência do Dockerfile

O projeto usa uma **construção multi-estágio** para tamanho de imagem otimizado:

```dockerfile
# Estágio de construção
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de execução
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

ENV TZ=UTC
```

**Especificações da Imagem:**
- **Imagem Base**: Eclipse Temurin 21 (Alpine)
- **Ferramenta de Build**: Maven 3.9
- **Porta Exposta**: 8081
- **Ponto de Entrada**: Execução do JAR Java

---

## Serviços do Docker Compose

O `docker-compose.yaml` inclui os seguintes serviços:

### 1. Banco de Dados PostgreSQL
```yaml
Serviço: postgres
Porta: 5432
Banco de dados: education_spaces_db
Usuário: postgres
Senha: postgres
```

### 2. Aplicação
```yaml
Serviço: app
Porta: 8081
Perfil: dev (padrão)
Health Check: Habilitado
```

### 3. SonarQube (Qualidade de Código)
```yaml
Serviço: sonarqube
Porta: 9000
Banco de dados: sonarqube-db (PostgreSQL)
```

### 4. Prometheus (Coleta de Métricas)
```yaml
Serviço: prometheus
Porta: 9090
Config: ./prometheus.yml
```

### 5. Grafana (Visualização de Métricas)
```yaml
Serviço: grafana
Porta: 3000
Usuário: admin
Senha: admin
```

---

## Variáveis de Ambiente

### Variáveis Obrigatórias

| Variável | Descrição | Padrão | Obrigatória |
|----------|-----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | Perfil ativo (dev/prod) | dev | Sim |
| `SPRING_DATASOURCE_URL` | URL JDBC do banco de dados | - | Sim |
| `SPRING_DATASOURCE_USERNAME` | Nome de usuário do banco | - | Sim |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | - | Sim |
| `JWT_SECRET` | Chave de assinatura JWT (mín 32 caracteres) | - | Sim |

### Variáveis Opcionais

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| `JWT_EXPIRATION` | Expiração do token (ms) | 3600000 |
| `SERVER_PORT` | Porta do servidor | 8081 |
| `TZ` | Fuso horário | UTC |

---

## Comandos Docker - Guia Rápido

### Comandos Docker Compose

```bash
# Iniciar todos os serviços
docker-compose up -d

# Iniciar serviço específico
docker-compose up -d app

# Ver logs (todos os serviços)
docker-compose logs -f

# Ver logs (serviço específico)
docker-compose logs -f app

# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v

# Reiniciar um serviço
docker-compose restart app

# Verificar status dos serviços
docker-compose ps

# Executar comando no contêiner
docker-compose exec app sh

# Reconstruir serviço
docker-compose up -d --build app
```

### Comandos Docker Standalone

```bash
# Construir imagem
docker build -t space-management:latest .

# Executar contêiner
docker run -d --name space-management -p 8081:8081 space-management:latest

# Parar contêiner
docker stop space-management

# Iniciar contêiner
docker start space-management

# Remover contêiner
docker rm space-management

# Ver logs
docker logs -f space-management

# Executar comando no contêiner
docker exec -it space-management sh

# Inspecionar contêiner
docker inspect space-management

# Ver estatísticas do contêiner
docker stats space-management
```

---

## Configuração de Monitoramento com Docker

### 1. Configurar Prometheus

O arquivo `prometheus.yml` deve conter:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'space-management'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8081']
```

### 2. Acessar Ferramentas de Monitoramento

Após executar `docker-compose up -d`:

**Prometheus:**
- URL: http://localhost:9090
- Exemplo de consulta: `space_entry_total`

**Grafana:**
- URL: http://localhost:3000
- Nome de usuário: `admin`
- Senha: `admin`
- Adicionar fonte de dados Prometheus: `http://prometheus:9090`

**Métricas da Aplicação:**
- URL: http://localhost:8081/actuator/prometheus

---

## Solução de Problemas

### Contêiner Não Inicia

```bash
# Verificar logs
docker-compose logs app

# Problemas comuns:
# 1. Banco de dados não está pronto
docker-compose logs postgres

# 2. Porta já em uso
lsof -i :8081
kill -9 <PID>

# 3. Variáveis de ambiente ausentes
docker-compose config
```

### Problemas de Conexão com Banco de Dados

```bash
# Verificar se PostgreSQL está executando
docker-compose ps postgres

# Testar conexão com banco de dados
docker-compose exec postgres psql -U postgres -d education_spaces_db

# Verificar conectividade de rede
docker-compose exec app ping postgres
```

### Falha no Health Check da Aplicação

```bash
# Verificar endpoint de saúde
curl http://localhost:8081/actuator/health

# Verificar se aplicação está respondendo
docker-compose exec app wget -O- http://localhost:8081/actuator/health

# Ver logs detalhados
docker-compose logs -f --tail=100 app
```

### Resetar Tudo

```bash
# Parar todos os serviços e remover volumes
docker-compose down -v

# Remover todas as imagens relacionadas
docker images | grep space-management | awk '{print $3}' | xargs docker rmi -f

# Começar do zero
docker-compose up -d --build
```

---

## Implantação em Produção

### Usando Docker em Produção

Para implantação em produção, crie um `docker-compose.prod.yaml`:

```yaml
version: '3.8'

services:
  app:
    image: space-management:1.0.0
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://prod-db-host:5432/education_spaces_db
      DATABASE_USERNAME: ${DB_USER}
      DATABASE_PASSWORD: ${DB_PASS}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: 3600000
    ports:
      - "8080:8081"
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

Execute com:
```bash
docker-compose -f docker-compose.prod.yaml up -d
```

### Melhores Práticas para Produção

1. **Use tags de imagem específicas**, não `latest`
2. **Defina limites de recursos** (CPU, memória)
3. **Habilite health checks**
4. **Use gerenciamento de secrets** para dados sensíveis
5. **Habilite políticas de reinicialização**
6. **Use bancos de dados gerenciados externos** (não contêineres)
7. **Configure agregação de logs** (ELK, Splunk)
8. **Monitore com Prometheus + Grafana**
9. **Use proxy reverso** (Nginx, Traefik)
10. **Habilite HTTPS/TLS**

---

## Otimização do Tamanho da Imagem Docker

Tamanho atual da imagem: ~300MB

Para reduzir ainda mais o tamanho:

```dockerfile
# Use JRE ao invés de JDK
FROM eclipse-temurin:21-jre-alpine

# Ou use JRE customizado com jlink
FROM eclipse-temurin:21-jdk-alpine AS jlink
RUN jlink --add-modules java.base,java.logging,java.sql \
    --output /custom-jre \
    --compress=2 \
    --no-header-files \
    --no-man-pages

FROM alpine:latest
COPY --from=jlink /custom-jre /opt/jre
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["/opt/jre/bin/java", "-jar", "app.jar"]
```

---

## Resumo

✅ **Início Rápido**: `docker-compose up -d`  
✅ **Acessar App**: http://localhost:8081  
✅ **Acessar Swagger**: http://localhost:8081/swagger-ui.html  
✅ **Ver Métricas**: http://localhost:9090 (Prometheus)  
✅ **Visualizar Dados**: http://localhost:3000 (Grafana)  
✅ **Qualidade de Código**: http://localhost:9000 (SonarQube)  
✅ **Parar Tudo**: `docker-compose down`

Para mais detalhes, veja o arquivo [docker-compose.yaml](docker-compose.yaml).

## 📚 Documentação da API

### Swagger UI

Acesse a documentação interativa da API:

```
http://localhost:8081/swagger-ui.html
```

### Especificação OpenAPI

- **JSON**: `http://localhost:8081/api-docs`
- **YAML**: `http://localhost:8081/api-docs.yaml`

### Início Rápido com Swagger

1. Abra o Swagger UI no seu navegador
2. Navegue para **Authentication** → **POST /api/auth/login**
3. Clique em **"Try it out"**
4. Insira as credenciais:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Clique em **"Execute"** e copie o token
6. Clique no botão **"Authorize"** (🔒) no topo
7. Insira: `Bearer {seu-token}`
8. Agora você pode testar todos os endpoints protegidos!

### Coleção Postman

Importe a especificação OpenAPI no Postman:

```bash
# Baixar especificação
curl http://localhost:8081/api-docs > space-management-api.json

# Importar no Postman: File → Import → Upload Files
```

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Classe de Teste Específica

```bash
mvn test -Dtest=StudentServiceTest
```

### Executar Testes de Integração

```bash
mvn test -Dtest=*IntegrationTest
```

### Gerar Relatório de Cobertura

```bash
mvn clean test jacoco:report
```

Ver relatório em: `target/site/jacoco/index.html`

### Análise de Qualidade de Código

```bash
# Iniciar SonarQube (se executando localmente)
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Executar análise
mvn clean verify sonar:sonar
```

Ver relatório em: `http://localhost:9000`

### Resumo de Cobertura de Testes

O projeto mantém os seguintes limites de cobertura:

- **Cobertura de Linha**: Mínimo 50%
- **Exclui**: DTOs, Entities, classes de Configuração

## 📊 Monitoramento

### Endpoints do Actuator

Disponível em `http://localhost:8081/actuator`

| Endpoint | Descrição |
|----------|-----------|
| `/actuator/health` | Status de saúde da aplicação |
| `/actuator/info` | Informações da aplicação |
| `/actuator/metrics` | Métricas da aplicação |
| `/actuator/prometheus` | Métricas Prometheus |
| `/actuator/env` | Propriedades do ambiente |
| `/actuator/loggers` | Configuração de loggers |

### Verificação de Saúde

```bash
curl http://localhost:8081/actuator/health
```

**Resposta:**
```json
{
  "status": "UP",
  "components": {
    "customHealth": {
      "status": "UP",
      "details": {
        "students": 10,
        "spaces": 5,
        "database": "Connected"
      }
    },
    "db": {
      "status": "UP"
    }
  }
}
```

### Métricas

```bash
# Ver todas as métricas
curl http://localhost:8081/actuator/metrics

# Métrica específica
curl http://localhost:8081/actuator/metrics/space.entry.total
```

### Integração com Prometheus

A aplicação expõe métricas compatíveis com Prometheus:

```bash
curl http://localhost:8081/actuator/prometheus
```

**Métricas de exemplo:**
- `space_entry_total`: Número total de entradas em espaços
- `space_exit_total`: Número total de saídas de espaços
- `students_active_total`: Estudantes ativos atualmente
- `spaces_available_total`: Espaços disponíveis
- `access_active_current`: Acessos ativos atuais

## 🔐 Segurança

### Fluxo de Autenticação

1. **Registrar** um novo estudante:
   ```bash
   POST /api/auth/register
   ```

2. **Login** para obter token JWT:
   ```bash
   POST /api/auth/login
   ```

3. **Usar token** em requisições subsequentes:
   ```bash
   Authorization: Bearer {token}
   ```

### Funções de Usuário

| Função | Permissões |
|--------|------------|
| **STUDENT** | Ver dados próprios, registrar acesso, ver relatórios |
| **ADMIN** | Acesso completo a todos os endpoints |

### Recursos de Segurança

- ✅ Hash de senha com BCrypt
- ✅ Autenticação baseada em token JWT
- ✅ Expiração de token (1 hora em prod, 24 horas em dev)
- ✅ Controle de acesso baseado em funções (RBAC)
- ✅ Proteção CORS
- ✅ Proteção CSRF desabilitada (API REST stateless)
- ✅ Prevenção de injeção SQL (JPA/Hibernate)
- ✅ Validação de entrada

### Usuários Padrão

Para fins de desenvolvimento/teste:

```json
{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN"
}
```

**⚠️ Aviso**: Altere as credenciais padrão em produção!

## 📁 Estrutura do Projeto

```
space-management/
├── src/
│   ├── main/
│   │   ├── java/dev/victor_rivas/space_management/
│   │   │   ├── config/              # Classes de configuração
│   │   │   │   ├── AppInfoContributor.java
│   │   │   │   ├── CorsProperties.java
│   │   │   │   ├── CustomHealthIndicator.java
│   │   │   │   ├── MetricsConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── constant/            # Constantes
│   │   │   │   └── ExceptionMessagesConstants.java
│   │   │   ├── controller/          # Controllers REST
│   │   │   │   ├── AccessRecordController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SpaceController.java
│   │   │   │   └── StudentController.java
│   │   │   ├── enums/               # Enumerações
│   │   │   │   ├── AccessStatus.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── SpaceStatus.java
│   │   │   │   ├── SpaceType.java
│   │   │   │   └── StudentStatus.java
│   │   │   ├── exception/           # Tratamento de exceções
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   │   ├── AccessRecordDTO.java
│   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── CreateStudentRequest.java
│   │   │   │   │   ├── EntryRequest.java
│   │   │   │   │   ├── ExitRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── OccupancyReportDTO.java
│   │   │   │   │   ├── SpaceDTO.java
│   │   │   │   │   └── StudentDTO.java
│   │   │   │   └── entity/          # Entidades JPA
│   │   │   │       ├── AccessRecord.java
│   │   │   │       ├── Space.java
│   │   │   │       ├── Student.java
│   │   │   │       └── User.java
│   │   │   ├── repository/          # Camada de Acesso a Dados
│   │   │   │   ├── AccessRecordRepository.java
│   │   │   │   ├── SpaceRepository.java
│   │   │   │   ├── StudentRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # Configuração de Segurança
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── service/             # Lógica de Negócio
│   │   │   │   ├── AccessRecordService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── MetricsService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   ├── SpaceService.java
│   │   │   │   └── StudentService.java
│   │   │   └── SpaceManagementApplication.java
│   │   └── resources/
│   │       ├── application.yaml           # Configuração base
│   │       ├── application-dev.yaml       # Configuração dev
│   │       ├── application-test.yaml      # Configuração test
│   │       └── application-prod.yaml      # Configuração prod
│   └── test/
│       └── java/dev/victor_rivas/space_management/
│           ├── integration/               # Testes de integração
│           │   ├── AccessRecordControllerIntegrationTest.java
│           │   ├── AuthControllerIntegrationTest.java
│           │   ├── ReportControllerIntegrationTest.java
│           │   ├── SpaceControllerIntegrationTest.java
│           │   └── StudentControllerIntegrationTest.java
│           └── SpaceManagementApplicationTests.java
├── .env.example                      # Template de variáveis de ambiente
├── .gitignore
├── pom.xml                          # Configuração Maven
├── prometheus.yml                   # Configuração Prometheus
├── sonar-project.properties         # Configuração SonarQube
└── README.md
```

## 🌐 Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login e obter token JWT | Público |
| POST | `/api/auth/register` | Registrar novo estudante | Público |

### Estudantes

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/students` | Obter todos os estudantes | ADMIN |
| GET | `/api/students/{id}` | Obter estudante por ID | Autenticado |
| PUT | `/api/students/{id}` | Atualizar estudante | Autenticado |
| DELETE | `/api/students/{id}` | Deletar estudante | ADMIN |

### Espaços

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/spaces` | Obter todos os espaços | Autenticado |
| GET | `/api/spaces/{id}` | Obter espaço por ID | Autenticado |
| POST | `/api/spaces` | Criar novo espaço | ADMIN |
| PUT | `/api/spaces/{id}` | Atualizar espaço | ADMIN |
| DELETE | `/api/spaces/{id}` | Deletar espaço | ADMIN |

### Registros de Acesso

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/access/entry` | Registrar entrada | Autenticado |
| POST | `/api/access/exit` | Registrar saída | Autenticado |
| GET | `/api/access` | Obter todos os registros | ADMIN |
| GET | `/api/access/student/{id}` | Obter registros por estudante | Autenticado |
| GET | `/api/access/space/{id}` | Obter registros por espaço | Autenticado |
| GET | `/api/access/active` | Obter registros ativos | Autenticado |

### Relatórios

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/reports/occupancy` | Obter ocupação de todos os espaços | Autenticado |
| GET | `/api/reports/occupancy/space/{id}` | Obter ocupação por espaço | Autenticado |

### Exemplos de Requisições

#### Registrar Estudante
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "registrationNumber": "STU001",
    "name": "João Silva",
    "email": "joao.silva@universidade.edu",
    "password": "senha123",
    "phoneNumber": "+5511987654321"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "STU001",
    "password": "senha123"
  }'
```

#### Criar Espaço (Admin)
```bash
curl -X POST http://localhost:8081/api/spaces \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "LAB-001",
    "name": "Laboratório de Computação",
    "type": "LABORATORY",
    "capacity": 30,
    "building": "Prédio A",
    "floor": "1º Andar"
  }'
```

#### Registrar Entrada
```bash
curl -X POST http://localhost:8081/api/access/entry \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "spaceId": 1,
    "notes": "Sessão de estudo"
  }'
```

## 🗄️ Esquema do Banco de Dados

### Diagrama de Relacionamento de Entidades

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   Student   │       │ AccessRecord │       │    Space    │
├─────────────┤       ├──────────────┤       ├─────────────┤
│ id (PK)     │───┐   │ id (PK)      │   ┌───│ id (PK)     │
│ regNumber   │   └──→│ student_id   │   │   │ code        │
│ name        │       │ space_id     │←──┘   │ name        │
│ email       │       │ entryTime    │       │ type        │
│ phoneNumber │       │ exitTime     │       │ capacity    │
│ status      │       │ status       │       │ building    │
│ createdAt   │       │ notes        │       │ floor       │
└─────────────┘       │ createdAt    │       │ status      │
                      └──────────────┘       └─────────────┘
       │
       │
       ↓
┌─────────────┐
│    User     │
├─────────────┤
│ id (PK)     │
│ username    │
│ email       │
│ password    │
│ role        │
│ student_id  │
│ enabled     │
└─────────────┘
```

### Tabelas

#### students
```sql
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### spaces
```sql
CREATE TABLE spaces (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    capacity INTEGER NOT NULL,
    building VARCHAR(50),
    floor VARCHAR(20),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### access_records
```sql
CREATE TABLE access_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    space_id BIGINT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (space_id) REFERENCES spaces(id)
);
```

#### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL,
    student_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id)
);
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga estes passos:

### 1. Fork do Repositório

Clique no botão "Fork" no canto superior direito da página do repositório.

### 2. Clone seu Fork

```bash
git clone https://github.com/seu-usuario/space-management.git
cd space-management
```

### 3. Criar um Branch

```bash
git checkout -b feature/nome-da-sua-feature
```

### 4. Faça suas Alterações

- Escreva código limpo e documentado
- Siga o estilo de código existente
- Adicione testes para novas funcionalidades
- Atualize a documentação

### 5. Execute os Testes

```bash
mvn clean test
```

### 6. Commit suas Alterações

```bash
git add .
git commit -m "Add: descrição das suas alterações"
```

### 7. Push para seu Fork

```bash
git push origin feature/nome-da-sua-feature
```

### 8. Criar um Pull Request

Vá para o repositório original e crie um pull request a partir do seu fork.

### Padrões de Codificação

- **Java**: Siga as Convenções de Código Java
- **Nomenclatura**: Use nomes descritivos para classes, métodos e variáveis
- **Comentários**: Documente lógica complexa e APIs públicas
- **Testes**: Mantenha no mínimo 50% de cobertura de código
- **Commits**: Use mensagens de commit claras e descritivas

## 📄 Licença

Este projeto está licenciado sob a Licença Apache 2.0 - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
Copyright 2024 Victor Rivas

Licenciado sob a Licença Apache, Versão 2.0 (a "Licença");
você não pode usar este arquivo exceto em conformidade com a Licença.
Você pode obter uma cópia da Licença em

    http://www.apache.org/licenses/LICENSE-2.0

A menos que exigido por lei aplicável ou acordado por escrito, o software
distribuído sob a Licença é distribuído "COMO ESTÁ",
SEM GARANTIAS OU CONDIÇÕES DE QUALQUER TIPO, expressas ou implícitas.
Consulte a Licença para o idioma específico que rege as permissões e
limitações sob a Licença.
```

## 👥 Autores

- **Victor Rivas** - *Trabalho inicial* - [@victor-rivas-dev](https://github.com/victor-rivas-dev)