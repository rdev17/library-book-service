package com.deloitte.library.book.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Member {
    private String userId;
    private String firstName;
    private String lastName;
    private Integer checkedOutCount;

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getCheckedOutCount() {
        return checkedOutCount;
    }

    public void setCheckedOutCount(Integer checkedOutCount) {
        this.checkedOutCount = checkedOutCount;
    }
}
