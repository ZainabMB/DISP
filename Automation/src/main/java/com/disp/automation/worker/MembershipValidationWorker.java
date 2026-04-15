package com.disp.automation.worker;

import com.disp.automation.service.MemberValidationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class MembershipValidationWorker {

    private static final Logger logger = LoggerFactory.getLogger(MembershipValidationWorker.class);

    private final MemberValidationService memberValidationService;

    public MembershipValidationWorker(MemberValidationService memberValidationService) {
        this.memberValidationService = memberValidationService;
    }

    @JobWorker(type = "membershipValidation")
    public void handleMembershipValidation(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        logger.info("Worker triggered — job key: {}", job.getKey());
        logger.info("Variables received: {}", job.getVariablesAsMap());

        Object raw = variablesAsMap.get("membershipNumber");
        Object ismember = variablesAsMap.get("ismember");
        Long membershipNumber = null;

        if (raw != null) {
            try {
                membershipNumber = Long.valueOf(raw.toString());
            } catch (NumberFormatException e) {
                logger.warn("Invalid membershipNumber format: {}", raw);
            }
        }

        HashMap<String, Object> variables = new HashMap<>();

        if (membershipNumber == null) {
            // No membership number provided — not a member
            variables.put("ismember", "no_member");
        } else {
            // Look up in DB
            boolean isMember = memberValidationService.validateMember(membershipNumber);
            variables.put("ismember", "yes_member");
        }

        logger.info("Membership validation — membershipNumber: {}, ismember: {}", membershipNumber, variables.get("ismember"));

        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .join();
    }


}