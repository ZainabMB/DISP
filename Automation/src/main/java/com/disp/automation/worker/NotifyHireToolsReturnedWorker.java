package com.disp.automation.worker;

import com.disp.automation.service.NotifyHireToolsReturnedService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotifyHireToolsReturnedWorker {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHireToolsReturnedWorker.class);

    private final NotifyHireToolsReturnedService notifyHireToolsReturnedService;

    public NotifyHireToolsReturnedWorker(NotifyHireToolsReturnedService notifyHireToolsReturnedService) {
        this.notifyHireToolsReturnedService = notifyHireToolsReturnedService;
    }

    @JobWorker(type = "notifyHireToolsReturned", autoComplete = false)
    public void handleNotifyHireToolsReturned(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — hireToolsReturned. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        String orderId = (String) vars.get("orderId");

        // Call service
        notifyHireToolsReturnedService.sendHireToolsReturned(orderId, vars);

        // Complete job
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("hireToolsReturned message sent and HireOrder updated for orderId {}", orderId);
    }
}
