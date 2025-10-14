package com.deloitte.library.book.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(@NotBlank String bookId, @NotBlank String title, @NotBlank String author) {
}
