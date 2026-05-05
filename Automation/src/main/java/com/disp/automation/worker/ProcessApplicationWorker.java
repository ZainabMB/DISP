package com.disp.automation.worker;

import com.disp.automation.service.ProcessApplicationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessApplicationWorker {

    private static final Logger logger = LoggerFactory.getLogger(ProcessApplicationWorker.class);

    private final ProcessApplicationService processApplicationService;

    public ProcessApplicationWorker(ProcessApplicationService processApplicationService) {
        this.processApplicationService = processApplicationService;
    }

    @JobWorker(type = "processApplication", autoComplete = false)
    public void handleCreditApplication(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        Object raw = vars.get("membershipNumber");
        Long memberId = null;

        if (raw != null && !raw.toString().trim().isEmpty()) {
            try {
                memberId = Long.valueOf(raw.toString().trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid memberId format: {}", raw);
            }
        }

        HashMap<String, Object> result = new HashMap<>();

        try {
            boolean hasActivePlan = processApplicationService.hasActiveCreditPlan(memberId);

            if (hasActivePlan) {
                result.put("processApplication", "denied");
                result.put("needFinance", false);
                result.put("paymentCompleted", false);// reset this so the gateway routes correctly
                logger.info("Member {} already has an active credit plan — denied", memberId);
            } else {
                result.put("processApplication", "approved");
                result.put("paymentCompleted", true);
                logger.info("Member {} has no active credit plan — approved", memberId);
            }

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("processApplication failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }}

