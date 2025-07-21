# Aplicação Spring Boot com Dead Letter Queue (DLQ) - RabbitMQ

Uma aplicação Java Spring Boot que implementa mensageria com Dead Letter Queue usando RabbitMQ, seguindo princípios de **Clean Architecture**, **DDD** (Domain-Driven Design) e **Clean Code**.

## 🏗️ Arquitetura

A aplicação segue o padrão **Arquitetura Hexagonal (Ports & Adapters)** com separação clara entre:

- **Core (Application)**: Contém a lógica de negócio, comandos, domínio e portas
- **Driven Adapters**: Implementações de infraestrutura (repositórios, mensageria)
- **Driving Adapters**: Interfaces de entrada (HTTP controllers)

### Estrutura de Pacotes

```
├── application/
│   ├── commands/           # Comandos (RecordExpense, RecordIncome)
│   ├── domain/            # Entidades de domínio e regras de negócio
│   ├── handlers/          # Casos de uso e lógica de aplicação
│   └── ports/
│       ├── inbound/       # Portas de entrada (interfaces)
│       └── outbound/      # Portas de saída (interfaces)
├── driven/                # Adaptadores de infraestrutura
│   ├── messaging/         # Configuração e implementação RabbitMQ
│   └── repository/        # Implementação de repositórios
└── driving/               # Adaptadores de entrada
    └── http/              # Controllers REST
```

## 🚀 Funcionalidades

1. **Processamento de Mensagens**: Consome mensagens de filas RabbitMQ para despesas e receitas
2. **Dead Letter Queue**: Mensagens com falha são automaticamente redirecionadas para DLQ
3. **Simulação de Falhas**: Mensagens contendo "erro" ou "error" são rejeitadas
4. **API REST**: Endpoints para enviar mensagens para as filas
5. **Monitoramento**: Logs detalhados e endpoints de saúde

## 🛠️ Pré-requisitos

- **Java 21**
- **RabbitMQ** (rodando na porta 5672)
- **Gradle**

### Iniciando RabbitMQ com Docker

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

Acesse o management UI em: http://localhost:15672 (guest/guest)

## 🔧 Configuração

A aplicação está configurada no `application.yml`:

```yaml
# Configurações do RabbitMQ
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

# Configurações das filas
rabbitmq:
  queues:
    expense:
      name: expense.queue
      dlq: expense.dlq
      exchange: expense.exchange
      routing-key: expense.routing.key
      dlq-routing-key: expense.dlq.routing.key
    income:
      name: income.queue
      dlq: income.dlq
      exchange: income.exchange
      routing-key: income.routing.key
      dlq-routing-key: income.dlq.routing.key
```

## 🚀 Executando a Aplicação

1. **Clone o repositório**
2. **Inicie o RabbitMQ** (ver seção pré-requisitos)
3. **Execute a aplicação**:

```bash
./gradlew bootRun
```

A aplicação estará disponível em: http://localhost:8080

## 📋 Testando a Aplicação

### Endpoints Disponíveis

#### Status da Aplicação
```bash
GET /api/test/status
```

#### Testes Automatizados

**Teste de Despesa (Sucesso)**:
```bash
POST /api/test/expense/success
```

**Teste de Despesa (Falha → DLQ)**:
```bash
POST /api/test/expense/error
```

**Teste de Receita (Sucesso)**:
```bash
POST /api/test/income/success
```

**Teste de Receita (Falha → DLQ)**:
```bash
POST /api/test/income/error
```

#### Endpoints de Produção

**Registrar Despesa**:
```bash
POST /api/expenses
Content-Type: application/json

{
  "description": "Compra de materiais",
  "amount": 150.75,
  "category": "Material"
}
```

**Registrar Receita**:
```bash
POST /api/incomes
Content-Type: application/json

{
  "description": "Consultoria",
  "amount": 500.00,
  "source": "Cliente A"
}
```

### Exemplos de Teste

#### 1. Teste com Sucesso
```bash
curl -X POST http://localhost:8080/api/test/expense/success
```

**Resultado esperado**: Mensagem processada com sucesso

#### 2. Teste com Falha (DLQ)
```bash
curl -X POST http://localhost:8080/api/test/expense/error
```

**Resultado esperado**: Mensagem vai para a Dead Letter Queue

#### 3. Teste Manual com Palavra "erro"
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Despesa com erro no sistema",
    "amount": 100.00,
    "category": "Teste"
  }'
```

**Resultado esperado**: Mensagem rejeitada e enviada para DLQ

## 🔍 Monitoramento

### Logs da Aplicação
Os logs mostram o fluxo completo das mensagens:

```
INFO  - Received expense message: {"id":"...","description":"..."}
INFO  - Processing expense: RecordExpense{...}
INFO  - Expense processed successfully: abc-123
```

Para mensagens com erro:
```
ERROR - Business logic error processing expense message
WARN  - Publishing message to expense DLQ due to processing failure
INFO  - Message successfully published to expense DLQ
WARN  - Received message in expense DLQ: {...}
```

### RabbitMQ Management
Acesse http://localhost:15672 para monitorar:
- Filas criadas automaticamente
- Mensagens processadas
- Mensagens na DLQ
- Estatísticas de throughput

## 🧪 Cenários de Teste

### Cenário 1: Processamento Normal
1. Envie uma despesa sem palavras de erro
2. Verifique logs de processamento bem-sucedido
3. Confirme que a mensagem não aparece na DLQ

### Cenário 2: Falha de Negócio
1. Envie uma despesa com "erro" na descrição
2. Verifique logs de falha de processamento
3. Confirme que a mensagem aparece na DLQ
4. Verifique o processamento da mensagem DLQ

### Cenário 3: Teste de Volume
```bash
# Execute múltiplas requisições
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/test/expense/success
  curl -X POST http://localhost:8080/api/test/expense/error
done
```

## 🏛️ Princípios Implementados

### Clean Architecture
- **Separação de responsabilidades**: Core isolado de infraestrutura
- **Dependency Inversion**: Dependências apontam para abstrações
- **Ports & Adapters**: Interfaces bem definidas entre camadas

### Domain-Driven Design (DDD)
- **Rich Domain Model**: Entidades com comportamento e validações
- **Ubiquitous Language**: Nomenclatura consistente
- **Domain Services**: Lógica de negócio encapsulada

### Clean Code
- **Nomes significativos**: Classes e métodos auto-explicativos
- **Funções pequenas**: Responsabilidade única
- **Tratamento de erros**: Exceções específicas do domínio

## 🔧 Personalização

### Adicionando Novas Regras de Falha
Edite o método `containsErrorKeyword()` nas entidades de domínio:

```java
public boolean containsErrorKeyword() {
    return description.toLowerCase().contains("erro") || 
           description.toLowerCase().contains("error") ||
           description.toLowerCase().contains("falha"); // Nova regra
}
```

### Configurando Retry Policy
Ajuste o `application.yml`:

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        retry:
          enabled: true
          max-attempts: 5          # Número de tentativas
          initial-interval: 2000   # Intervalo inicial
```

## 📚 Conceitos Demonstrados

1. **Mensageria Assíncrona**: Processamento desacoplado via filas
2. **Dead Letter Queue**: Tratamento robusto de falhas
3. **Clean Architecture**: Separação limpa de responsabilidades  
4. **Domain-Driven Design**: Modelagem rica do domínio
5. **Dependency Injection**: Inversão de controle
6. **Configuration Management**: Configuração externa
7. **Error Handling**: Tratamento de exceções estruturado
8. **Logging**: Observabilidade e debugging

---

Esta aplicação demonstra uma implementação robusta de mensageria com DLQ, seguindo as melhores práticas de arquitetura de software e permitindo fácil manutenção e extensibilidade.