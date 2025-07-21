#!/bin/bash

# Script de teste para a aplicação DLQ

echo "=== Testes da Aplicação DLQ com RabbitMQ ==="
echo ""

BASE_URL="http://localhost:8080"

# Teste 1: Status da aplicação
echo "1. Verificando status da aplicação..."
curl -X GET $BASE_URL/api/test/status
echo -e "\n"

# Teste 2: Despesa com sucesso
echo "2. Testando despesa com sucesso..."
curl -X POST $BASE_URL/api/test/expense/success
echo -e "\n"

# Teste 3: Despesa com erro (DLQ)
echo "3. Testando despesa com erro (vai para DLQ)..."
curl -X POST $BASE_URL/api/test/expense/error
echo -e "\n"

# Teste 4: Receita com sucesso
echo "4. Testando receita com sucesso..."
curl -X POST $BASE_URL/api/test/income/success
echo -e "\n"

# Teste 5: Receita com erro (DLQ)
echo "5. Testando receita com erro (vai para DLQ)..."
curl -X POST $BASE_URL/api/test/income/error
echo -e "\n"

# Teste 6: Despesa manual
echo "6. Testando despesa via endpoint manual..."
curl -X POST $BASE_URL/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Compra de material de escritório",
    "amount": 150.75,
    "category": "Material"
  }'
echo -e "\n"

# Teste 7: Despesa manual com erro
echo "7. Testando despesa manual com erro (DLQ)..."
curl -X POST $BASE_URL/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Despesa com erro no sistema",
    "amount": 99.99,
    "category": "Teste"
  }'
echo -e "\n"

# Teste 8: Receita manual
echo "8. Testando receita via endpoint manual..."
curl -X POST $BASE_URL/api/incomes \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Consultoria de desenvolvimento",
    "amount": 500.00,
    "source": "Cliente ABC"
  }'
echo -e "\n"

# Teste 9: Receita manual com erro
echo "9. Testando receita manual com erro (DLQ)..."
curl -X POST $BASE_URL/api/incomes \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Receita com error no processamento",
    "amount": 300.00,
    "source": "TestSource"
  }'
echo -e "\n"

echo "=== Testes concluídos ==="
echo "Verifique os logs da aplicação para ver o processamento das mensagens"
echo "Acesse http://localhost:15672 (guest/guest) para ver as filas no RabbitMQ"