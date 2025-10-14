package com.deloitte.library.book.repository;

import com.deloitte.library.book.model.Checkout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Repository
public class CheckoutRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<Checkout> checkoutTable;

    public CheckoutRepository(final DynamoDbClient dynamoDbClient, final DynamoDbTable<Checkout> checkoutTable) {
        this.dynamoDbClient = dynamoDbClient;
        this.checkoutTable = checkoutTable;
    }

    public TransactWriteItemsResponse checkoutBook(final String userId, final String title, final String author,
                                                   final String isbn) {
        final LocalDateTime currentTs = LocalDateTime.now();

        final var bookInventoryUpdateRequest = TransactWriteItem.builder()
                .update(updateBuilder -> updateBuilder
                        .tableName("books")
                        .key(Map.of("title", AttributeValue.fromS(title),
                                    "author", AttributeValue.fromS(author)))
                        .updateExpression("SET inventory = inventory - :reduction")
                        .expressionAttributeValues(Map.of(":reduction", AttributeValue.fromN("1")))
                        .build())
                .build();

        final var checkoutUpdateRequest = TransactWriteItem.builder()
                .put(putBuilder -> putBuilder
                        .tableName("checkouts")
                        .item(Map.of(
                                "userId", AttributeValue.fromS(userId),
                                "bookId", AttributeValue.fromS(isbn),
                                "checkoutStatus", AttributeValue.fromS("Active"),
                                "checkoutTimestamp", AttributeValue.fromS(
                                        currentTs.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))),
                                "returnDueDate", AttributeValue.fromS(
                                        currentTs.toLocalDate().plusDays(14L).format(DateTimeFormatter.ISO_DATE)))))
                .build();

        final var memberCheckoutCountUpdateRequest = TransactWriteItem.builder()
                .update(updateBuilder -> updateBuilder
                        .tableName("members")
                        .key(Map.of("userId", AttributeValue.fromS(userId)))
                        .conditionExpression("checkedOutCount < :limit")
                        .updateExpression("SET checkedOutCount = checkedOutCount + :amount")
                        .expressionAttributeValues(Map.of(":limit", AttributeValue.fromN("5"),
                                                          ":amount", AttributeValue.fromN("1")))
                        .build())
                .build();

        final TransactWriteItemsRequest transactionsRequest = TransactWriteItemsRequest.builder()
                .transactItems(bookInventoryUpdateRequest, checkoutUpdateRequest, memberCheckoutCountUpdateRequest)
                .build();

        return this.dynamoDbClient.transactWriteItems(transactionsRequest);
    }

    public TransactWriteItemsResponse returnBook(final String userId, final String title, final String author,
                                                 final String bookId) {
        final var bookInventoryUpdateRequest = TransactWriteItem.builder()
                .update(updateBuilder -> updateBuilder
                        .tableName("books")
                        .key(Map.of("title", AttributeValue.fromS(title),
                                    "author", AttributeValue.fromS(author)))
                        .updateExpression("SET inventory = inventory + :increment")
                        .expressionAttributeValues(Map.of(":increment", AttributeValue.fromN("1")))
                        .build())
                .build();

        final var checkoutUpdateRequest = TransactWriteItem.builder()
                .update(updateBuilder -> updateBuilder
                        .tableName("checkouts")
                        .key(Map.of("userId", AttributeValue.fromS(userId),
                                    "bookId", AttributeValue.fromS(bookId)))
                        .updateExpression("SET checkoutStatus = :returnedStatus, expiresAt = :expirationDate REMOVE checkoutTimestamp, returnDueDate")
                        .expressionAttributeValues(Map.of(":returnedStatus", AttributeValue.fromS("Returned"),
                                                          ":expirationDate", AttributeValue.fromS(LocalDateTime.now()
                                        .plusMinutes(3L)
                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                        .build())
                .build();

        final var memberCheckoutCountUpdateRequest = TransactWriteItem.builder()
                .update(updateBuilder -> updateBuilder
                        .tableName("members")
                        .key(Map.of("userId", AttributeValue.fromS(userId)))
                        .updateExpression("SET checkedOutCount = checkedOutCount - :amount")
                        .expressionAttributeValues(Map.of(":amount", AttributeValue.fromN("1")))
                        .build())
                .build();

        final TransactWriteItemsRequest transactWriteItemsRequest = TransactWriteItemsRequest.builder()
                .transactItems(bookInventoryUpdateRequest, checkoutUpdateRequest, memberCheckoutCountUpdateRequest)
                .build();

        return this.dynamoDbClient.transactWriteItems(transactWriteItemsRequest);
    }

    public Optional<Checkout> fetchCheckout(final String userId, final String bookId) {
        try {
            return Optional.of(this.checkoutTable.getItem(Key.builder()
                    .partitionValue(userId)
                    .sortValue(bookId)
                    .build()));
        } catch (RuntimeException e) {
            LOGGER.warn("Error occurred while attempting to fetch record from DynamoDB table", e);
            return Optional.empty();
        }
    }
}
