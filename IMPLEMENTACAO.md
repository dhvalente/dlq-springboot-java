# Documentação da Implementação - DLQ com Spring Boot

## 📋 Resumo da Implementação

Esta aplicação demonstra uma implementação completa de **Dead Letter Queue (DLQ)** usando **Spring Boot** e **RabbitMQ**, seguindo rigorosamente os princípios de **Clean Architecture**, **DDD** e **Clean Code**.

## 🏗️ Arquitetura Implementada

### Clean Architecture (Arquitetura Hexagonal)

```
┌─────────────────────────────────────────────────────────────┐
│                    DRIVING ADAPTERS                         │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  HTTP REST API  │  │  Test Controller │                  │
│  │ ExpenseEndpoint │  │ TestController   │                  │
│  │ IncomeEndpoint  │  └─────────────────┘                  │
│  └─────────────────┘                                        │
└─────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION CORE                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │    COMMANDS     │  │     DOMAIN      │                  │
│  │ RecordExpense   │  │    Expense      │                  │
│  │ RecordIncome    │  │    Income       │                  │
│  └─────────────────┘  │ FinancialProc.. │                  │
│                       └─────────────────┘                  │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │    HANDLERS     │  │     PORTS       │                  │
│  │ ExpenseHandler  │  │ ExpenseUseCase  │                  │
│  │ IncomeHandler   │  │ IncomeUseCase   │                  │
│  └─────────────────┘  │ Repositories... │                  │
│                       └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    DRIVEN ADAPTERS                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   MESSAGING     │  │  REPOSITORIES   │                  │
│  │ RabbitConsumer  │  │ InMemoryRepo    │                  │
│  │ RabbitPublisher │  │ ...             │                  │
│  │ RabbitConfig    │  └─────────────────┘                  │
│  └─────────────────┘                                        │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo de Mensageria

### Fluxo Normal (Sucesso)
```
HTTP Request → Controller → UseCase → Domain Logic → Repository
                                   ↓
RabbitMQ Queue → Consumer → UseCase → Domain → Success
```

### Fluxo com Falha (DLQ)
```
HTTP Request → Controller → UseCase → Domain Logic → Exception
                                   ↓
RabbitMQ Queue → Consumer → UseCase → Domain → Exception → DLQ Publisher → DLQ Queue
```

## 📦 Estrutura de Pacotes Detalhada

```
src/main/java/br/com/suit/DLQ/
├── application/                    # Core da aplicação
│   ├── commands/                   # Comandos (Data Transfer Objects)
│   │   ├── RecordExpense.java     # Comando para despesas
│   │   └── RecordIncome.java      # Comando para receitas
│   ├── domain/                     # Entidades de domínio
│   │   ├── Expense.java           # Entidade Despesa
│   │   ├── ExpenseStatus.java     # Enum de status
│   │   ├── Income.java            # Entidade Receita
│   │   ├── IncomeStatus.java      # Enum de status
│   │   └── FinancialProcessingException.java # Exceção de domínio
│   ├── handlers/                   # Casos de uso (Application Services)
│   │   ├── ExpenseHandler.java    # Processamento de despesas
│   │   └── IncomeHandler.java     # Processamento de receitas
│   └── ports/                      # Interfaces (Ports)
│       ├── inbound/                # Portas de entrada
│       │   ├── ExpenseProcessingUseCase.java
│       │   └── IncomeProcessingUseCase.java
│       └── outbound/               # Portas de saída
│           ├── ExpenseRepository.java
│           ├── IncomeRepository.java
│           └── DeadLetterQueuePublisher.java
├── driven/                         # Adaptadores de infraestrutura
│   ├── messaging/                  # Infraestrutura de mensageria
│   │   ├── config/
│   │   │   └── RabbitMQConfig.java # Configuração das filas
│   │   ├── consumer/               # Consumidores
│   │   │   ├── ExpenseConsumer.java
│   │   │   └── IncomeConsumer.java
│   │   └── publisher/              # Publicadores
│   │       └── RabbitDeadLetterQueuePublisher.java
│   └── repository/                 # Implementação de repositórios
│       ├── InMemoryExpenseRepository.java
│       └── InMemoryIncomeRepository.java
└── driving/                        # Adaptadores de entrada
    └── http/                       # Controllers REST
        ├── finances/
        │   ├── expense/
        │   │   ├── ExpenseEndpoint.java
        │   │   ├── Request.java
        │   │   └── jsons/           # Exemplos de JSON
        │   └── income/
        │       ├── IncomeEndpoint.java
        │       └── Request.java
        └── testes/
            └── TestController.java  # Controller para testes
```

## 🔧 Configurações Implementadas

### application.yml
- Configuração completa do RabbitMQ
- Configuração de filas principais e DLQ
- Configuração de retry policy
- Configuração de logging
- Configuração de health checks

### RabbitMQ Configuration
- Exchanges diretos para expense e income
- Filas principais com DLQ automática
- Bindings configurados
- Dead Letter Exchange configurado

## 🚦 Cenários de Teste Implementados

### 1. Teste de Sucesso
- Mensagem processada normalmente
- Salva no repositório
- Não vai para DLQ

### 2. Teste de Falha (DLQ)
- Mensagem contendo "erro" ou "error"
- Processamento falha
- Automaticamente enviada para DLQ
- DLQ processa mensagem para análise

### 3. Endpoints de Teste
- `/api/test/expense/success` - Despesa com sucesso
- `/api/test/expense/error` - Despesa com falha
- `/api/test/income/success` - Receita com sucesso
- `/api/test/income/error` - Receita com falha
- `/api/test/status` - Status da aplicação

### 4. Endpoints de Produção
- `/api/expenses` - POST para registrar despesa
- `/api/incomes` - POST para registrar receita

## 🎯 Princípios Aplicados

### Clean Code
✅ **Nomes Significativos**: Todas as classes e métodos têm nomes auto-explicativos
✅ **Funções Pequenas**: Cada método tem responsabilidade única
✅ **Sem Comentários Desnecessários**: Código auto-documentado
✅ **Tratamento de Erros**: Exceções específicas do domínio
✅ **Formatação Consistente**: Padrão em toda a aplicação

### Domain-Driven Design (DDD)
✅ **Rich Domain Model**: Entidades com comportamento e validações
✅ **Ubiquitous Language**: Terminologia consistente
✅ **Domain Services**: Lógica complexa encapsulada
✅ **Value Objects**: Objetos imutáveis para valores
✅ **Domain Exceptions**: Exceções específicas do negócio

### Clean Architecture
✅ **Dependency Inversion**: Core independente de frameworks
✅ **Separation of Concerns**: Camadas bem definidas
✅ **Ports & Adapters**: Interfaces bem definidas
✅ **Testabilidade**: Fácil criação de testes unitários
✅ **Flexibilidade**: Fácil troca de implementações

## 🔍 Monitoramento e Observabilidade

### Logging Implementado
- Logs estruturados com SLF4J
- Diferentes níveis por pacote
- Rastreamento completo do fluxo
- Logs específicos para DLQ

### Métricas Disponíveis
- Spring Boot Actuator
- Health checks
- Metrics endpoint
- RabbitMQ management UI

## 🧪 Como Testar

### 1. Iniciar RabbitMQ
```bash
docker-compose up -d
```

### 2. Executar Aplicação
```bash
./gradlew bootRun
```

### 3. Executar Testes
```bash
./test-commands.sh
```

### 4. Monitorar
- Logs da aplicação no console
- RabbitMQ Management: http://localhost:15672
- Application Health: http://localhost:8080/actuator/health

## 🎉 Resultados Esperados

### Processamento Normal
```
INFO - Received expense message: {...}
INFO - Processing expense: RecordExpense{...}
INFO - Expense processed successfully: abc-123
```

### Processamento com Falha (DLQ)
```
ERROR - Business logic error processing expense message
WARN  - Publishing message to expense DLQ due to processing failure
INFO  - Message successfully published to expense DLQ
WARN  - Received message in expense DLQ: {...}
```

## 📚 Conceitos Demonstrados

1. **Mensageria Assíncrona** - Processamento desacoplado via filas
2. **Dead Letter Queue** - Tratamento robusto de falhas
3. **Clean Architecture** - Separação limpa de responsabilidades
4. **Domain-Driven Design** - Modelagem rica do domínio
5. **Dependency Injection** - Inversão de controle
6. **Configuration Management** - Configuração externa
7. **Error Handling** - Tratamento estruturado de exceções
8. **Observability** - Logging e monitoramento

---

Esta implementação serve como exemplo prático de como construir aplicações robustas e maintíveis seguindo as melhores práticas de arquitetura de software.