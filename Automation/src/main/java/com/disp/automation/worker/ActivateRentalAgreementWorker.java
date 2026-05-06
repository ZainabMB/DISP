package com.disp.automation.worker;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.service.ActivateRentalAgreementService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActivateRentalAgreementWorker {

    private static final Logger logger = LoggerFactory.getLogger(ActivateRentalAgreementWorker.class);

    private final ActivateRentalAgreementService activateRentalAgreementService;

    public ActivateRentalAgreementWorker(ActivateRentalAgreementService activateRentalAgreementService) {
        this.activateRentalAgreementService = activateRentalAgreementService;
    }

    @JobWorker(type = "activateRentalAgreement", autoComplete = false)
    public void handleActivateRentalAgreement(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — activating rental agreement. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        String orderId = (String) vars.get("orderId");

        if (orderId == null) {
            logger.error("orderId is null — cannot activate rental agreement");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        HireOrder updatedOrder = activateRentalAgreementService.activateRentalAgreement(orderId);

        // Add updated status back into process variables
        vars.put("status", updatedOrder.getStatus().name());

        client.newCompleteCommand(job.getKey())
                .variables(vars)
                .send()
                .join();

        logger.info("Rental agreement activated for orderId {}", orderId);
    }
}
