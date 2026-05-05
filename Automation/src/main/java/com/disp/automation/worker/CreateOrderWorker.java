package com.disp.automation.worker;

import com.disp.automation.service.CreateOrderService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateOrderWorker {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderWorker.class);

    private final CreateOrderService createOrderService;

    public CreateOrderWorker(CreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }

    @JobWorker(type = "createOrder", autoComplete = false)
    public void handleCreateOrder(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("createOrder worker triggered — job key: {}", job.getKey());

        try {
            String orderId = createOrderService.createOrder(vars);
            logger.info("Order created successfully — orderId: {}", orderId);

            client.newCompleteCommand(job.getKey())
                    .variable("orderId", orderId)
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("createOrder failed: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}