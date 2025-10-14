package com.deloitte.library.book.controller;

import com.deloitte.library.book.dto.BookInfo;
import com.deloitte.library.book.dto.ImageMetadataDto;
import com.deloitte.library.book.dto.UploadBookRequest;
import com.deloitte.library.book.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/library/books")
@Validated
public class BookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private static final String HEADER_KEY_USER_ID = "user-id";
    private static final Set<String> VALID_USER_IDS = Set.of("user123", "test123");

    private final BookService bookService;

    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Hello World!";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadBook(@RequestHeader(HEADER_KEY_USER_ID) @NotBlank final String userId,
                                           @RequestPart(name = "requestJson") @Valid final UploadBookRequest request,
                                           @RequestPart(name = "file", required = false) final MultipartFile file,
                                           @RequestPart(name = "metadata", required = false) @Valid final ImageMetadataDto metadata) {
        LOGGER.debug("Entering LibraryBookController.uploadBook");

        if (VALID_USER_IDS.contains(userId)) {
            this.bookService.uploadBook(request, file, metadata);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return sendUserUnauthorizedResponse(userId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookInfo> fetchBook(@RequestHeader(HEADER_KEY_USER_ID) @NotBlank final String userId,
                                              @RequestParam @NotBlank final String title,
                                              @RequestParam @NotBlank final String author) {
        LOGGER.debug("Entering LibraryBookController.fetchBook");

        if (VALID_USER_IDS.contains(userId)) {
           return ResponseEntity.ok(this.bookService.fetchBook(title, author));
        }

        return sendUserUnauthorizedResponse(userId);
    }

    private static <T> ResponseEntity<T> sendUserUnauthorizedResponse(final String userId) {
        LOGGER.warn("User is not authorized, unauthorized user ID: {}", userId);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
