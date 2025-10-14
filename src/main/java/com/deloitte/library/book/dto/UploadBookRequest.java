package com.deloitte.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UploadBookRequest(@NotBlank String title, @NotBlank String author, @NotBlank String isbn,
                                @NotNull LocalDate releaseDate, @NotNull Integer inventory) {
}
