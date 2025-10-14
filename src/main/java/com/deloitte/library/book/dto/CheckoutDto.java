package com.deloitte.library.book.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CheckoutDto(String bookId, String checkoutStatus, LocalDateTime checkoutTimestamp,
                          LocalDate returnDueDate) {
}
