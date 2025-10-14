package com.deloitte.library.book.dto;

import java.time.LocalDate;

public record BookInfo(String title, String author, String isbn, LocalDate releaseDate, Integer inventory) {
}
