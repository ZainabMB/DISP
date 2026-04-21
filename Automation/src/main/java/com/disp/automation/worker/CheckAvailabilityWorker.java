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
public class CheckAvailabilityWorker {

    private static final Logger logger = LoggerFactory.getLogger(CheckAvailabilityWorker.class);
    private final CheckAvailabilityService checkAvailabilityService;

    public CheckAvailabilityWorker(CheckAvailabilityService checkAvailabilityService) {
        this.checkAvailabilityService = checkAvailabilityService;
    }

    @JobWorker(type = "checkAvailability", autoComplete = false)
    public void handleCheckAvailability(final JobClient client, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        String toolType = (String) variables.get("toolType");
        String toolName = (String) variables.get("toolName");
        Object quantityRaw = variables.get("quantity");

        logger.info("toolType: {}, toolName: {}, quantity: {}", toolType, toolName, quantityRaw);

        HashMap<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> availableTools = checkAvailabilityService.getToolsByType(toolType);
            logger.info("availableTools returned: {}", availableTools);
            result.put("availableTools", availableTools);

            if (toolName != null && !toolName.isEmpty() && quantityRaw != null) {
                int quantity = Integer.parseInt(quantityRaw.toString());
                boolean itemAvailable = checkAvailabilityService.isQuantityAvailable(toolName, quantity);
                result.put("itemAvailable", itemAvailable);

                Double totalPrice = checkAvailabilityService.calculateTotalPrice(toolName, quantity);
                result.put("totalPrice", totalPrice);
            }

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("checkAvailability failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}