package com.disp.automation.worker;

import com.disp.automation.service.ProcessPaymentService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProcessPaymentWorker {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentWorker.class);
    private final ProcessPaymentService processPaymentService;

    public ProcessPaymentWorker(ProcessPaymentService processPaymentService) {
        this.processPaymentService = processPaymentService;
    }

    @JobWorker(type = "processPayment", autoComplete = false)
    public void processPayment(final JobClient client, final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("processPayment triggered — vars: {}", vars);

        try {
            Map<String, Object> result = processPaymentService.processPayment(vars);
            logger.info("Payment result: {}", result);

            client.newCompleteCommand(job.getKey())
                    .variables(result)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("processPayment failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}