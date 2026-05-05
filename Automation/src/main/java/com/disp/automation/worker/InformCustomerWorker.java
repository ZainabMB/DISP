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

        String orderId = (String) vars.get("orderId");

        if (orderId == null) {
            logger.error("orderId is null — cannot send order ready message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        logger.info("Sending order ready message for orderId: {}", orderId);

        try {
            informCustomerService.sendOrderReady(orderId, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("Order ready message sent successfully for orderId: {}", orderId);

        } catch (Exception e) {
            logger.error("informCustomer failed for orderId {}: {}", orderId, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}
