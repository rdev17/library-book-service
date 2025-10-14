package com.deloitte.library.book.service;

import com.deloitte.library.book.dto.BookInfo;
import com.deloitte.library.book.dto.ImageMetadataDto;
import com.deloitte.library.book.dto.UploadBookRequest;
import com.deloitte.library.book.model.Book;
import com.deloitte.library.book.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Service
public class BookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void uploadBook(final UploadBookRequest uploadBookRequest, final MultipartFile file,
                           final ImageMetadataDto metadataDto) {
        LOGGER.debug("Entering BookService.uploadBook...");
        this.bookRepository.save(toBookEntity(uploadBookRequest));
    }

    public BookInfo fetchBook(final String title, final String author) {
        LOGGER.debug("Entering BookService.fetchBook...");
        return this.bookRepository.findByKey(title, author)
                .map(BookService::toBookDto)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("No book found with given title: %s and given author: %s".formatted(title, author))
                        .build());
    }

    /**
     * Converts DAO to DTO {@link #toBookDto}
     *
     * @param uploadBookRequest The DTO coming in from the endpoint
     * @return converted DAO
     */
    private static Book toBookEntity(final UploadBookRequest uploadBookRequest) {
        final Book bookEntity = new Book();
        bookEntity.setTitle(uploadBookRequest.title());
        bookEntity.setAuthor(uploadBookRequest.author());
        bookEntity.setIsbn(uploadBookRequest.isbn());
        bookEntity.setReleaseDate(uploadBookRequest.releaseDate());
        bookEntity.setInventory(uploadBookRequest.inventory());

        return bookEntity;
    }

    private static BookInfo toBookDto(final Book book) {
        return new BookInfo(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getReleaseDate(),
                            book.getInventory());
    }
}
