package com.disp.automation.worker;

import com.disp.automation.service.SendCreditApplicationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendCreditApplicationWorker {

    private static final Logger logger = LoggerFactory.getLogger(SendCreditApplicationWorker.class);

    private final SendCreditApplicationService sendCreditApplicationService;

    public SendCreditApplicationWorker(SendCreditApplicationService sendCreditApplicationService) {
        this.sendCreditApplicationService = sendCreditApplicationService;
    }

    @JobWorker(type = "sendCreditApplication", autoComplete = false)
    public void handleSendCreditApplication(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending credit application to FinTrust. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Extract applicationId
        String applicationId = (String) vars.get("applicationId");

        logger.info("Sending message with applicationId: {}", applicationId);

        // Send message to FinTrust
        sendCreditApplicationService.sendCreditApplication(applicationId, vars);

        // Complete the task
        client.newCompleteCommand(job.getKey())
                .send()
                .join();

        logger.info("Credit application message sent successfully for applicationId {}", applicationId);
    }
}
