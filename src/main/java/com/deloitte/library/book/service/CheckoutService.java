package com.deloitte.library.book.service;

import com.deloitte.library.book.dto.CheckoutDto;
import com.deloitte.library.book.model.Checkout;
import com.deloitte.library.book.repository.CheckoutRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Service
public class CheckoutService {
    private final CheckoutRepository checkoutRepository;

    public CheckoutService(final CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    public void checkoutBook(final String userId, final String title, final String author,
                             final String isbn) {
        this.checkoutRepository.checkoutBook(userId, title, author, isbn);
    }

    public void returnBook(final String userId, final String title, final String author, String bookId) {
        this.checkoutRepository.returnBook(userId, title, author, bookId);
    }

    public CheckoutDto fetchCheckout(final String userId, final String bookId) {
        return this.checkoutRepository.fetchCheckout(userId, bookId)
                .map(CheckoutService::toCheckoutDto)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("Could not find checkout with partition key: %s and sort key: %s"
                                .formatted(userId, bookId))
                        .build());
    }

    private static CheckoutDto toCheckoutDto(final Checkout checkout) {
        return new CheckoutDto(checkout.getBookId(), checkout.getCheckoutStatus(),
                               checkout.getCheckoutTimestamp(), checkout.getReturnDueDate());
    }
}
