package com.deloitte.library.book.service;

import com.deloitte.library.book.dto.MemberDto;
import com.deloitte.library.book.model.Member;
import com.deloitte.library.book.repository.MemberRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDto fetchMember(final String userId) {
        return this.memberRepository.getMemberByKey(userId)
                .map(MemberService::toMemberDto)
                .orElseThrow(() -> ResourceNotFoundException.builder()
                        .message("User with ID: %s, was not found".formatted(userId))
                        .build());
    }

    private static MemberDto toMemberDto(final Member member) {
        return new MemberDto(member.getUserId(), member.getFirstName(), member.getLastName(),
                             member.getCheckedOutCount());
    }
}
