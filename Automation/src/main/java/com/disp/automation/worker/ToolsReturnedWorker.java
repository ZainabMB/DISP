package com.disp.automation.worker;

import com.disp.automation.service.ToolsReturnedService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ToolsReturnedWorker {

    private static final Logger logger = LoggerFactory.getLogger(ToolsReturnedWorker.class);

    private final ToolsReturnedService toolsReturnedService;

    public ToolsReturnedWorker(ToolsReturnedService toolsReturnedService) {
        this.toolsReturnedService = toolsReturnedService;
    }

    @JobWorker(type = "ToolsReturned", autoComplete = false)
    public void handleToolsReturned(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending ToolsReturned message. Job key: {}", job.getKey());

        String orderId = vars.get("orderId") != null
                ? vars.get("orderId").toString() : null;

        if (orderId == null) {
            logger.error("orderId is null — cannot send ToolsReturned message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        try {
            toolsReturnedService.sendToolsReturned(orderId, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("ToolsReturned message sent successfully for orderId: {}", orderId);

        } catch (Exception e) {
            logger.error("ToolsReturned failed for orderId {}: {}", orderId, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}