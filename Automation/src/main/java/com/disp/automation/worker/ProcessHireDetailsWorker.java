package com.disp.automation;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ProcessHireDetailsWorker {

    private final ProcessHireDetailsService processHireDetailsService;

    //inject service
    public ProcessHireDetailsWorker(ProcessHireDetailsService processHireDetailsService) {
        this.processHireDetailsService = processHireDetailsService;
    }

    @JobWorker(type = "processHireDetails")
    public void processHireDetails(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        //read all variables set by the confirm tool hire form
        String customerName  = (String) vars.getOrDefault("customerName", "unknown");
        String toolName      = (String) vars.getOrDefault("toolName", "unknown");
        String hireStartDate = (String) vars.getOrDefault("hireStartDate", "");
        String hireEndDate   = (String) vars.getOrDefault("hireEndDate", "");
        double depositAmount = ((Number) vars.getOrDefault("depositAmount", 0.0)).doubleValue();
        String phone         = (String) vars.getOrDefault("phone", "");
        String email         = (String) vars.getOrDefault("email", "");

        //call service to handle the logic
        boolean hireConfirmed = processHireDetailsService.validateHireDetails(
                customerName, toolName, hireStartDate, hireEndDate, depositAmount, phone, email
        );

        client.newCompleteCommand(job.getKey())
                .variable("hireConfirmed", hireConfirmed)
                .variable("rentalEnd", hireEndDate)
                .variable("customerName", customerName)
                .variable("toolName", toolName)
                .variable("depositAmount", depositAmount)
                .variable("customerPhone", phone)
                .variable("customerEmail", email)
                .send().join();
    }
}