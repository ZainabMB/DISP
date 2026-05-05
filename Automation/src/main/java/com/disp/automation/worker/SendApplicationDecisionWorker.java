package com.disp.automation.worker;

import com.disp.automation.service.SendApplicationDecisionService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendApplicationDecisionWorker {

    private static final Logger logger = LoggerFactory.getLogger(SendApplicationDecisionWorker.class);

    private final SendApplicationDecisionService sendApplicationDecisionService;

    public SendApplicationDecisionWorker(SendApplicationDecisionService sendApplicationDecisionService) {
        this.sendApplicationDecisionService = sendApplicationDecisionService;
    }

    @JobWorker(type = "sendApplicationDecision", autoComplete = false)
    public void handleSendApplicationDecision(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();

        String applicationId = vars.get("application_id") != null
                ? vars.get("application_id").toString()
                : vars.get("applicationId") != null
                ? vars.get("applicationId").toString()
                : null;

        logger.info("Sending application decision — applicationId: {}", applicationId);

        if (applicationId == null) {
            logger.error("applicationId is missing — cannot correlate message");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("applicationId is missing")
                    .send()
                    .join();
            return;
        }

        try {
            sendApplicationDecisionService.sendApplicationDecision(applicationId, vars);
            logger.info("ApplicationDecisionReceived message published successfully for applicationId: {}", applicationId);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

        } catch (Exception e) {
            logger.error("Failed to send application decision: {}", e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}