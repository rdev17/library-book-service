package com.deloitte.library.book.controller;

import com.deloitte.library.book.dto.CheckoutDto;
import com.deloitte.library.book.dto.CheckoutRequest;
import com.deloitte.library.book.service.CheckoutService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/library/checkouts")
@Validated
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(final CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> checkoutBook(@RequestHeader @NotBlank final String userId,
                                                            @RequestBody @Valid final CheckoutRequest request) {
        this.checkoutService.checkoutBook(userId, request.title(), request.author(), request.bookId());
        return new ResponseEntity<>(Map.of("message", "Success"), HttpStatus.CREATED);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> returnBook(@RequestHeader @NotBlank final String userId,
                                                          @RequestBody @Valid final CheckoutRequest request) {
        this.checkoutService.returnBook(userId, request.title(), request.author(), request.bookId());
        return new ResponseEntity<>(Map.of("message", "Success"), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CheckoutDto> fetchCheckout(@RequestHeader(name = "user-id") @NotBlank final String userId,
                                                     @RequestParam @NotBlank final String bookId) {
        return ResponseEntity.ok(this.checkoutService.fetchCheckout(userId, bookId));
    }
}
