package com.disp.automation.worker;

import com.disp.automation.entity.Member;
import com.disp.automation.service.MemberRegistrationService;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberRegistrationWorker {

    private static final Logger logger = LoggerFactory.getLogger(MemberRegistrationWorker.class);

    private final MemberRegistrationService memberRegistrationService;

    public MemberRegistrationWorker(MemberRegistrationService memberRegistrationService) {
        this.memberRegistrationService = memberRegistrationService;
    }

    @JobWorker(type = "membershipRegistration", autoComplete = false)
    public void handleMemberRegistration(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        String firstName    = (String) variables.get("firstName");
        String lastName     = (String) variables.get("lastName");
        String dateOfBirth  = (String) variables.get("dateOfBirth");
        String email        = (String) variables.get("emailAddress");
        String phoneNumber  = (String) variables.get("phoneNumber");

        Map<String, Object> result = new HashMap<>();

        try {
            Member saved = memberRegistrationService.registerMember(
                    firstName, lastName, dateOfBirth, email, phoneNumber
            );

            logger.info("Member registered successfully with ID: {}", saved.getMemberId());

            result.put("membershipNumber", saved.getMemberId());
            result.put("ismember", "yes_member");

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("Failed to register member: {}", e.getMessage());

            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Registration failed: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}