package com.disp.automation.worker;

import com.disp.automation.service.CheckAvailabilityService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreditApplicationWorker {

    private static final Logger logger = LoggerFactory.getLogger(CreditApplicationWorker.class);

    private final CreditApplicationService creditApplicationService;

    public CreditApplicationWorker(CreditApplicationService creditApplicationService) {
        this.creditApplicationService = creditApplicationService;
    }

    @JobWorker(type = "processApplication", autoComplete = false)
    public void handleCreditApplication(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        Object raw = vars.get("memberId");
        Long memberId = null;

        if (raw != null && !raw.toString().trim().isEmpty()) {
            try {
                memberId = Long.valueOf(raw.toString().trim());
            } catch (NumberFormatException e) {
                logger.warn("Invalid memberId format: {}", raw);
            }
        }

        HashMap<String, Object> result = new HashMap<>();

        boolean hasActivePlan = creditApplicationService.hasActiveCreditPlan(memberId);

        if (hasActivePlan) {
            result.put("processApplication", "denied");
            logger.info("Member {} has active credit plan — denied", memberId);
        } else {
            result.put("processApplication", "approved");
            logger.info("Member {} has no active credit plan — approved", memberId);
        }

        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }
}

