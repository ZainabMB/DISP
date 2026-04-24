package com.disp.automation;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ProcessPaymentWorker {

    private final ProcessPaymentService processPaymentService;

    //inject service
    public ProcessPaymentWorker(ProcessPaymentService processPaymentService) {
        this.processPaymentService = processPaymentService;
    }

    @JobWorker(type = "processPayment")
    public void processPayment(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        //read variables set by the form
        double orderTotal    = ((Number) vars.getOrDefault("orderTotal", 0.0)).doubleValue();
        String paymentMethod = (String) vars.getOrDefault("paymentMethod", "unknown");
        String customerName  = (String) vars.getOrDefault("customerName", "");
        String cardNumber    = (String) vars.getOrDefault("cardNumber", "");
        String cardExpiry    = (String) vars.getOrDefault("cardExpiry", "");
        String cardCVV       = (String) vars.getOrDefault("cardCVV", "");

        //call service to handle the logic
        boolean paymentCompleted = processPaymentService.processPayment(
                orderTotal, paymentMethod, customerName, cardNumber, cardExpiry, cardCVV
        );

        client.newCompleteCommand(job.getKey())
                .variable("paymentCompleted", paymentCompleted)
                .variable("paymentMethod", paymentMethod)
                .variable("amountPaid", orderTotal)
                .send().join();
    }
}