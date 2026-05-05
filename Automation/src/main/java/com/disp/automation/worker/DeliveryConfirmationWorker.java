package com.disp.automation.worker;

import com.disp.automation.service.DeliveryConfirmationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeliveryConfirmationWorker {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryConfirmationWorker.class);

    private final DeliveryConfirmationService deliveryConfirmationService;

    public DeliveryConfirmationWorker(DeliveryConfirmationService deliveryConfirmationService) {
        this.deliveryConfirmationService = deliveryConfirmationService;
    }

    @JobWorker(type = "deliveryConfirmation", autoComplete = false)
    public void handleDeliveryConfirmation(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending delivery completed message. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract orderId
        String orderId = (String) vars.get("orderId");

        logger.info("Sending message with orderId: {}", orderId);
        if (orderId == null) {
            logger.error("orderId is null — cannot send delivery completed message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        // Send message
        deliveryConfirmationService.sendDeliveryCompleted(orderId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("Delivery completed message sent successfully for orderId {}", orderId);
    }
}
