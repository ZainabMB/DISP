package com.disp.automation.worker;

import com.disp.automation.service.SendRepairDecisionService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendRepairDecisionWorker {

    private static final Logger logger = LoggerFactory.getLogger(SendRepairDecisionWorker.class);

    private final SendRepairDecisionService sendRepairDecisionService;

    public SendRepairDecisionWorker(SendRepairDecisionService sendRepairDecisionService) {
        this.sendRepairDecisionService = sendRepairDecisionService;
    }

    @JobWorker(type = "sendRepairDecision", autoComplete = false)
    public void handleSendRepairDecision(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending repair decision. Job key: {}", job.getKey());

        String serialNumber = vars.get("serialNumber") != null
                ? vars.get("serialNumber").toString() : null;

        if (serialNumber == null) {
            logger.error("serialNumber is null — cannot send repair decision");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("serialNumber is missing")
                    .send()
                    .join();
            return;
        }

        try {
            sendRepairDecisionService.sendRepairDecision(serialNumber, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("Repair decision sent successfully for serialNumber: {}", serialNumber);

        } catch (Exception e) {
            logger.error("sendRepairDecision failed for serialNumber {}: {}", serialNumber, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}