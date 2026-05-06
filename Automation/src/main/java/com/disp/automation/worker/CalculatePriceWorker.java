package com.disp.automation.worker;

import com.disp.automation.service.CalculatePriceService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculatePriceWorker {

    private static final Logger logger = LoggerFactory.getLogger(CalculatePriceWorker.class);
    private final CalculatePriceService calculatePriceService;

    public CalculatePriceWorker(CalculatePriceService calculatePriceService) {
        this.calculatePriceService = calculatePriceService;
    }

    @JobWorker(type = "calculatePrice", autoComplete = false)
    public void handleCalculatePrice(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        String distributionType = vars.get("distributionType") != null
                ? vars.get("distributionType").toString() : "pickup";
        String toolType        = (String) vars.get("toolType");
        String toolName        = (String) vars.get("toolName");
        int quantity           = vars.get("quantity") != null
                ? Integer.parseInt(vars.get("quantity").toString()) : 1;
        int hireDays = vars.get("hireDays") != null
                ? Integer.parseInt(vars.get("hireDays").toString()) : 0;

        String membershipNumber = vars.get("membershipNumber") != null
                ? vars.get("membershipNumber").toString() : "no_member";
        boolean register_member = Boolean.parseBoolean(
                vars.getOrDefault("register_member", "false").toString()

        );

        logger.info("calculatePrice — toolType: {}, toolName: {}, quantity: {}, membershipNumber: {}, register_member: {}",
                toolType, toolName, quantity, membershipNumber, register_member);

        try {

            Map<String, Object> result = calculatePriceService.calculatePrice(
                    toolType, toolName, quantity, membershipNumber, register_member, hireDays, distributionType
            );

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("calculatePrice failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}