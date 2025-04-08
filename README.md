# 🚀 Projeto Java com Docker + ngrok

API REST em Java para integrar com a API do HubSpot,
implementando autenticação via OAuth 2.0, mais especificamente com o fluxo de
authorization code flow, a implementação de endpoint de integração com a API e o
recebimento de notificações via webhooks. 

---

## Pré-requisitos

Antes de executar o projeto, você precisa ter instalado:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Java 21+](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html) (ou compatível)
- [Hubspot Account](https://developers.hubspot.com/)
- [Ngrok Account](https://dashboard.ngrok.com/signup) [^1]

[^1]: Para teste local com docker não é necessário

---

## Tecnologias 
- **Java 21+**  
  Linguagem principal usada na aplicação, com suporte a novos recursos de performance e segurança.

- **Spring Boot**  
  Framework web usado para simplificar a criação de APIs REST com configuração mínima, integração com dependências e servidor embutido.

- **OAuth2 Client (Spring Security)**  
  Biblioteca utilizada para gerenciar autenticação e autorização via OAuth 2.0, integrando com provedores externos de identidade (como HubSpot, Google, etc.). 
  Seu uso facilita o gerenciamento do ciclo de vida dos tokens, permitindo que o esforço de desenvolvimento seja focado na resolução do problema.

- **Ngrok**  
  Ferramenta usada para expor a aplicação local de forma segura através de um domínio público. Usado exclusivamente para testar o webhook, pois o Hubspot 
  somente redireciona para domínios `https`

- **Docker & Docker Compose**  
  Usado para containerizar a aplicação e facilitar a execução em qualquer ambiente com todos os serviços orquestrados.

- **Swagger / OpenAPI**
  Usado para gerar a documentacao 
---
## Configuração
Antes de iniciar, é necessário configurar os tokens de segurança descritos no arquivo `.env.example`.  
Copie esse arquivo e renomeie para `.env`, preenchendo os valores com seus dados:

Há basicamente duas formas de executar a aplicacao: 
1. manual via IDE ou CLI 
2. docker-compose

O primeiro passo para ambos os mode é clonar o repositório 
```bash
git clone git@github.com:afonsoneto121/basic-api-hubspot.git &&
cd basic-api-hubspot
```
ou  
```bash
git clone https://github.com/afonsoneto121/basic-api-hubspot.git &&
cd basic-api-hubspot
```

Este guia limita-se no segundo modo, que dependendo do SO os comandos podem mudar. Para usuários Linux há um auto start.

### Se voce usar uma distribuicao Linux 
```bash
cdmod +x install.sh &&
./install.sh  
```

Isso ira iniciar o build da aplicação e download das imagens docker necessário, pode levar de 5 a 10 minutos para iniciar.

### Se voce usar Windows
```powershell
mvnw.cmd clean package -DskipTests
```
```powershell
docker compose up --build
```

Após a logo do Spring aparecer no console ja e posivel inciar os testes, que podem sem realizados por ferramentas como 
Postman e Insomnia ou via CLI com cURL. A forma mais simples e usar a pagina de documentacao do [Swagger](http://localhost:8080/swagger-ui/index.html)

## Teste 
A aplicação foi desenvolvida para seguir uma sequência específica de testes:

1. **`GET /v1/url-generate`**  
   Esse endpoint gera a URL de autenticação OAuth com o HubSpot.  
   Copie a URL retornada e cole no navegador. Você será redirecionado para a tela de login do HubSpot.  
   Após autenticar-se com suas credenciais, você verá a mensagem:  
   _"Autenticação concluída com sucesso! Você pode fechar esta guia."_  
   Isso indica que a aplicação foi autenticada corretamente e já pode acessar recursos protegidos. O token será salvo automaticamente na aplicação.

2. **`POST /v1/create-contact`**  
   Endpoint responsável por cadastrar um novo contato no HubSpot.  
   Ele só funcionará se a autenticação anterior tiver sido concluída com sucesso.  
   Se tudo ocorrer como esperado, a aplicação retornará os dados persistidos com status `201 Created`.

3. **Webhook (`/v1/webhook`)**  
   Após a criação de um contato, o HubSpot enviará um webhook para o endpoint configurado.  
   Como ainda não há uma lógica definida para esse fluxo, o payload recebido é apenas impresso nos logs da aplicação (marcado como `TODO`).

> ⚠️ **Importante:** os endpoints `v1/webhook` e `v1/auth-callback` só funcionarão corretamente com 
> dados reais enviados pelo HubSpot, contendo uma assinatura válida e um código de autenticação legítimo, respectivamente.

## Limitacoes e Melhorias
Como se trata de uma aplicação de teste, alguns pontos importantes merecem atenção:

1. **Autenticação centralizada com riscos de segurança**  
   Atualmente, após a autenticação via OAuth2 com o HubSpot, o token obtido é compartilhado por toda a aplicação.  
   Isso significa que qualquer usuário pode realizar requisições a recursos protegidos utilizando o token do usuário autenticado.  
   Essa abordagem é vulnerável em ambientes multiusuário e deve ser aprimorada com uma estratégia de autenticação por sessão ou por usuário.

2. **Armazenamento de tokens em memória**  
   A implementação padrão do `OAuth2Client` do Spring Security utiliza a classe `InMemoryOAuth2AuthorizedClientService`,  
   o que significa que os tokens são armazenados apenas em memória. Isso não é adequado para ambientes de produção.  
   O ideal seria substituir por `JdbcOAuth2AuthorizedClientService`, que persiste os dados em um banco de dados seguro,  
   ou implementar um serviço personalizado para armazenar os tokens em uma solução como Redis ou outro banco externo.

