package com.disp.automation.worker;

import com.disp.automation.service.MarkToolsAvailableService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MarkToolsAvailableWorker {

    private static final Logger logger = LoggerFactory.getLogger(MarkToolsAvailableWorker.class);

    private final MarkToolsAvailableService markToolsAvailableService;

    public MarkToolsAvailableWorker(MarkToolsAvailableService markToolsAvailableService) {
        this.markToolsAvailableService = markToolsAvailableService;
    }

    @JobWorker(type = "markToolsAvailable", autoComplete = false)
    public void handleMarkToolsAvailable(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — marking tools available/retired. Job key: {}", job.getKey());

        String orderId = vars.get("orderId") != null
                ? vars.get("orderId").toString() : null;

        String maintenanceOption = vars.get("maintenanceOption") != null
                ? vars.get("maintenanceOption").toString() : "unknown";

        if (orderId == null) {
            logger.error("orderId is null — cannot mark tools");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        logger.info("maintenanceOption: {} — orderId: {}", maintenanceOption, orderId);

        try {
            markToolsAvailableService.markToolsAvailable(orderId, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("Tools marked as {} for orderId: {}",
                    maintenanceOption.equalsIgnoreCase("decommissioned") ? "RETIRED" : "AVAILABLE",
                    orderId);

        } catch (Exception e) {
            logger.error("markToolsAvailable failed for orderId {}: {}", orderId, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}