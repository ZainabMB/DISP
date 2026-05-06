package com.disp.automation.worker;

import com.disp.automation.service.NotifyHireToolsReceivedService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotifyHireToolsReceivedWorker {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHireToolsReceivedWorker.class);

    private final NotifyHireToolsReceivedService notifyHireToolsReceivedService;

    public NotifyHireToolsReceivedWorker(NotifyHireToolsReceivedService notifyHireToolsReceivedService) {
        this.notifyHireToolsReceivedService = notifyHireToolsReceivedService;
    }

    @JobWorker(type = "notifyHireToolsReceived", autoComplete = false)
    public void handleNotifyHireToolsReceived(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending hireToolsReceived message. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract correlation key
        String orderId = (String) vars.get("orderId");

        if (orderId == null) {
            logger.error("orderId is null — cannot send hireToolsReceived message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        // Send message
        notifyHireToolsReceivedService.sendHireToolsReceived(orderId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("hireToolsReceived message sent successfully for orderId {}", orderId);
    }
}
