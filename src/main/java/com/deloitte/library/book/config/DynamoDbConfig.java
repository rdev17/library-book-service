package com.deloitte.library.book.config;

import com.deloitte.library.book.model.Book;
import com.deloitte.library.book.model.Checkout;
import com.deloitte.library.book.model.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(@Value("${aws.access-key-id") final String accessKey,
                                         @Value("${aws.secret-access-key}") final String secretKey,
                                         @Value("${aws.region}") final String region) {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localstack:4566"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(final DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<Book> bookTable(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table("books", TableSchema.fromBean(Book.class));
    }

    @Bean
    public DynamoDbTable<Checkout> checkoutTable(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table("checkouts", TableSchema.fromBean(Checkout.class));
    }

    @Bean
    public DynamoDbTable<Member> memberTable(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table("members", TableSchema.fromBean(Member.class));
    }
}
