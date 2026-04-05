package com.example.disp;
import io.camunda.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DispApplication {

    public static void main(String[] args) {
        SpringApplication.run(DispApplication.class, args);
    }
    // --- Static "database" of known members ---
    record Member(
            String firstName,
            String lastName,
            String dateOfBirth,
            String emailAddress,
            String phoneNumber,
            String membershipNumber,
            String membershipLevel,  // "GOLD", "SILVER", "BRONZE"
            String membershipStatus  // "ACTIVE", "EXPIRED", "SUSPENDED"
    ) {}

    private static final List<Member> MEMBER_DB = List.of(
            new Member("Alice", "Smith",   "1990-05-12", "alice@example.com", "07700900001", "MEM001", "GOLD",   "ACTIVE"),
            new Member("Bob",   "Jones",   "2008-03-22", "bob@example.com",   "07700900002", "MEM002", "SILVER", "ACTIVE"),
            new Member("Carol", "White",   "1985-11-30", "carol@example.com", "07700900003", "MEM003", "BRONZE", "EXPIRED"),
            new Member("Dave",  "Brown",   "2010-07-04", "dave@example.com",  "07700900004", "MEM004", "SILVER", "SUSPENDED")
    );

    @JobWorker(type = "membershipValidation")
    public void handleValidateMembership(final JobClient client, final ActivatedJob job) {

        System.out.println("membershipValidation job received!");

        Map<String, Object> vars = job.getVariablesAsMap();

        String firstName = (String) vars.get("firstName");
        String lastName = (String) vars.get("lastName");
        String membershipNumber = (String) vars.get("membershipNumber");


        Map<String, Object> result = new HashMap<>();

        Member found = MEMBER_DB.stream()
                .filter(m -> m.membershipNumber().equalsIgnoreCase(membershipNumber))
                .findFirst()
                .orElse(null);

        if (found == null) {

            result.put("ismember", false);
            System.out.println("❌ Membership not found: " + membershipNumber);

        } else if (!found.firstName().equalsIgnoreCase(firstName) ||
                !found.lastName().equalsIgnoreCase(lastName)) {

            result.put("ismember", false);
            System.out.println("❌ Name mismatch");

        } else if (found.membershipStatus().equals("EXPIRED") ||
                found.membershipStatus().equals("SUSPENDED")) {

            result.put("ismember", false);
            System.out.println("❌ Membership inactive");

        } else {

            result.put("ismember", true);
            System.out.println("✅ Membership valid");

        }

        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }}