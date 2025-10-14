package com.deloitte.library.book.repository;

import com.deloitte.library.book.model.Member;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
public class MemberRepository {
    private final DynamoDbTable<Member> memberTable;

    public MemberRepository(final DynamoDbTable<Member> memberTable) {
        this.memberTable = memberTable;
    }

    public Optional<Member> getMemberByKey(final String userId) {
        try {
            return Optional.of(this.memberTable.getItem(Key.builder().partitionValue(userId).build()));
        } catch (UnsupportedOperationException e) {
            return Optional.empty();
        }
    }
}
