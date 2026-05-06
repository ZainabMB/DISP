package com.disp.automation.worker;

import com.disp.automation.entity.Member;
import com.disp.automation.service.MemberValidationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MembershipValidationWorker {

    private static final Logger logger = LoggerFactory.getLogger(MembershipValidationWorker.class);

    private final MemberValidationService memberValidationService;

    public MembershipValidationWorker(MemberValidationService memberValidationService) {
        this.memberValidationService = memberValidationService;
    }

    @JobWorker(type = "membershipValidation", autoComplete = false)
    public void handleMembershipValidation(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        logger.info("Worker triggered — job key: {}", job.getKey());
        logger.info("Variables received: {}", variablesAsMap);

        Object raw = variablesAsMap.get("membershipNumber");
        String ismember = (String) variablesAsMap.get("ismember");
        Long membershipNumber = null;

        if (raw != null && !raw.toString().trim().isEmpty()) {
            try {
                membershipNumber = Long.valueOf(raw.toString().trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid membershipNumber format: {}", raw);
            }
        }

        HashMap<String, Object> variables = new HashMap<>();

        try {
            if ("no_member".equals(ismember)) {
                variables.put("ismember", "no_member");
                logger.info("Customer is not a member, skipping validation");

                client.newCompleteCommand(job.getKey())
                        .variables(variables)
                        .send()
                        .join();

            } else if ("yes_member".equals(ismember)) {
                Optional<Member> memberOpt = memberValidationService.validateMember(membershipNumber);

                if (memberOpt.isPresent()) {
                    Member member = memberOpt.get();
                    variables.put("ismember", "yes_member");
                    variables.put("membershipNumber", membershipNumber);
                    variables.put("firstName", member.getFirstname());
                    variables.put("lastName", member.getSurname());
                    logger.info("Valid member found — membershipNumber: {}, name: {} {}",
                            membershipNumber, member.getFirstname(), member.getSurname());

                    client.newCompleteCommand(job.getKey())
                            .variables(variables)
                            .send()
                            .join();

                } else {
                    logger.warn("Membership number {} not found in DB", membershipNumber);
                    client.newThrowErrorCommand(job.getKey())
                            .errorCode("invalid_membership_id")
                            .errorMessage("Membership number " + membershipNumber + " does not match any member")
                            .send()
                            .join();
                }
            }
        } catch (Exception e) {
            logger.error("membershipValidation failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}