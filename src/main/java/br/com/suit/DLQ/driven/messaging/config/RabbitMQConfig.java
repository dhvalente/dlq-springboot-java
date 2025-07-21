package br.com.suit.DLQ.driven.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Expense Queue Configuration
    @Value("${rabbitmq.queues.expense.name}")
    private String expenseQueueName;
    
    @Value("${rabbitmq.queues.expense.dlq}")
    private String expenseDlqName;
    
    @Value("${rabbitmq.queues.expense.exchange}")
    private String expenseExchangeName;
    
    @Value("${rabbitmq.queues.expense.routing-key}")
    private String expenseRoutingKey;
    
    @Value("${rabbitmq.queues.expense.dlq-routing-key}")
    private String expenseDlqRoutingKey;

    // Income Queue Configuration
    @Value("${rabbitmq.queues.income.name}")
    private String incomeQueueName;
    
    @Value("${rabbitmq.queues.income.dlq}")
    private String incomeDlqName;
    
    @Value("${rabbitmq.queues.income.exchange}")
    private String incomeExchangeName;
    
    @Value("${rabbitmq.queues.income.routing-key}")
    private String incomeRoutingKey;
    
    @Value("${rabbitmq.queues.income.dlq-routing-key}")
    private String incomeDlqRoutingKey;

    // Expense Exchange and Queues
    @Bean
    public DirectExchange expenseExchange() {
        return new DirectExchange(expenseExchangeName, true, false);
    }

    @Bean
    public Queue expenseQueue() {
        return QueueBuilder.durable(expenseQueueName)
                .withArgument("x-dead-letter-exchange", expenseExchangeName)
                .withArgument("x-dead-letter-routing-key", expenseDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue expenseDlq() {
        return QueueBuilder.durable(expenseDlqName).build();
    }

    @Bean
    public Binding expenseBinding() {
        return BindingBuilder.bind(expenseQueue()).to(expenseExchange()).with(expenseRoutingKey);
    }

    @Bean
    public Binding expenseDlqBinding() {
        return BindingBuilder.bind(expenseDlq()).to(expenseExchange()).with(expenseDlqRoutingKey);
    }

    // Income Exchange and Queues
    @Bean
    public DirectExchange incomeExchange() {
        return new DirectExchange(incomeExchangeName, true, false);
    }

    @Bean
    public Queue incomeQueue() {
        return QueueBuilder.durable(incomeQueueName)
                .withArgument("x-dead-letter-exchange", incomeExchangeName)
                .withArgument("x-dead-letter-routing-key", incomeDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue incomeDlq() {
        return QueueBuilder.durable(incomeDlqName).build();
    }

    @Bean
    public Binding incomeBinding() {
        return BindingBuilder.bind(incomeQueue()).to(incomeExchange()).with(incomeRoutingKey);
    }

    @Bean
    public Binding incomeDlqBinding() {
        return BindingBuilder.bind(incomeDlq()).to(incomeExchange()).with(incomeDlqRoutingKey);
    }
}