package com.disp.automation.worker;

import com.disp.automation.service.RentalAgreementActivatedService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RentalAgreementActivatedWorker {

    private static final Logger logger = LoggerFactory.getLogger(RentalAgreementActivatedWorker.class);

    private final RentalAgreementActivatedService rentalAgreementActivatedService;

    public RentalAgreementActivatedWorker(RentalAgreementActivatedService rentalAgreementActivatedService) {
        this.rentalAgreementActivatedService = rentalAgreementActivatedService;
    }

    @JobWorker(type = "rentalAgreementActivated", autoComplete = false)
    public void handleRentalAgreementActivated(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending rentalAgreementActivated message. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        String orderId = (String) vars.get("orderId");

        logger.info("Sending message with orderId: {}", orderId);

        // Publish BPMN message
        rentalAgreementActivatedService.sendRentalAgreementActivated(orderId, vars);

        // Complete the job
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("Message rentalAgreementActivated sent successfully for orderId {}", orderId);
    }
}
