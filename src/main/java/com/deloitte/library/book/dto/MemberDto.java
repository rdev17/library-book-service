package com.deloitte.library.book.dto;

public record MemberDto(String userId, String firstName, String lastName, Integer checkedOutCount) {
}
