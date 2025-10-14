package com.deloitte.library.book.repository;

import com.deloitte.library.book.model.Book;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
public class BookRepository {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<Book> bookTable;

    public BookRepository(final DynamoDbEnhancedClient dynamoDbEnhancedClient, final DynamoDbTable<Book> bookTable) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.bookTable = bookTable;
    }

    public void save(final Book book) {
        this.bookTable.putItem(book);
    }

    public Optional<Book> findByKey(final String title, final String author) {
        try {
            return Optional.of(
                    this.bookTable.getItem(
                            Key.builder()
                                    .partitionValue(title)
                                    .sortValue(author)
                                    .build()));
        } catch (UnsupportedOperationException e) {
            return Optional.empty();
        }
    }
}
