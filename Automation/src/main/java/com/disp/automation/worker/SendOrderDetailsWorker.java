package com.disp.automation.worker;

import com.disp.automation.service.SendOrderDetailsService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendOrderDetailsWorker {

    private static final Logger logger = LoggerFactory.getLogger(SendOrderDetailsWorker.class);

    private final SendOrderDetailsService sendOrderDetailsService;

    public SendOrderDetailsWorker(SendOrderDetailsService sendOrderDetailsService) {
        this.sendOrderDetailsService = sendOrderDetailsService;
    }

    @JobWorker(type = "sendOrderDetails", autoComplete = false)
    public void handleSendOrderDetails(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending order details. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract orderId
        String orderId = (String) vars.get("orderId");

        logger.info("Sending message with orderId: {}", orderId);
        if (orderId == null) {
            logger.error("orderId is null — cannot send order details");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        // Send message
        sendOrderDetailsService.sendOrderDetails(orderId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("Order details message sent successfully for orderId {}", orderId);
    }
}
