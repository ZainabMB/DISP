package com.disp.automation.worker;

import com.disp.automation.service.NotifyHireToolReadyService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotifyHireToolReadyWorker {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHireToolReadyWorker.class);

    private final NotifyHireToolReadyService notifyHireToolReadyService;

    public NotifyHireToolReadyWorker(NotifyHireToolReadyService notifyHireToolReadyService) {
        this.notifyHireToolReadyService = notifyHireToolReadyService;
    }

    @JobWorker(type = "notifyHireToolReady", autoComplete = false)
    public void handleNotifyHireToolReady(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending hireToolReady message. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract correlation key
        String orderId = (String) vars.get("orderId");

        if (orderId == null) {
            logger.error("orderId is null — cannot send hireToolReady message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        // Send message
        notifyHireToolReadyService.sendHireToolReady(orderId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("hireToolReady message sent successfully for orderId {}", orderId);
    }
}
