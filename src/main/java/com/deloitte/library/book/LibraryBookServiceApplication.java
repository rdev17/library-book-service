package com.deloitte.library.book;

import com.deloitte.library.book.model.Member;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@SpringBootApplication
public class LibraryBookServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryBookServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner createDefaultMember(final DynamoDbTable<Member> memberTable) {
		return args -> {
			final Member member = new Member();
			member.setFirstName("John");
			member.setLastName("Doe");
			member.setUserId("user123");
			member.setCheckedOutCount(0);

			int retries = 5;
			while (retries-- > 0) {
				try {
					memberTable.putItem(member);
					System.out.println("Default member was successfully added to the table");
					break;
				} catch (Exception e) {
					Thread.sleep(5000L);
				}
			}
		};
	}
}
