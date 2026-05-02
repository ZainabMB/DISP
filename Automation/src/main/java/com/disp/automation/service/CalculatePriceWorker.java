package com.disp.automation.worker;

import com.disp.automation.service.CalculatePriceService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculatePriceWorker {

    private final CalculatePriceService calculatePriceService;

    public CalculatePriceWorker(CalculatePriceService calculatePriceService) {
        this.calculatePriceService = calculatePriceService;
    }

    @JobWorker(type = "calculatePrice", autoComplete = false)
    public void handleCalculatePrice(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        String transactionType = (String) vars.get("transactionType");
        String toolName        = (String) vars.get("toolName");
        int    quantity        = vars.get("quantity") != null
                ? Integer.parseInt(vars.get("quantity").toString()) : 1;
        String ismember        = vars.get("ismember") != null
                ? vars.get("ismember").toString() : "no_member";

        Map<String, Object> result = calculatePriceService.calculatePrice(
                transactionType, toolName, quantity, ismember
        );

        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }
}