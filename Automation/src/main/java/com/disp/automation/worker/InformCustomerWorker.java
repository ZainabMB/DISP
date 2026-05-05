package com.disp.automation.worker;

import com.disp.automation.service.InformCustomerService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InformCustomerWorker {

    private static final Logger logger = LoggerFactory.getLogger(InformCustomerWorker.class);

    private final InformCustomerService informCustomerService;

    public InformCustomerWorker(InformCustomerService informCustomerService) {
        this.informCustomerService = informCustomerService;
    }

    @JobWorker(type = "informCustomer", autoComplete = false)
    public void handleInformCustomer(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — informing customer that order is ready. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract orderId
        String orderId = (String) vars.get("orderId");

        logger.info("Sending message with orderId: {}", orderId);
        if (orderId == null) {
            logger.error("orderId is null — cannot send order ready message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        // Send message
        informCustomerService.sendOrderReady(orderId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("Order ready message sent successfully for orderId {}", orderId);
    }
}
