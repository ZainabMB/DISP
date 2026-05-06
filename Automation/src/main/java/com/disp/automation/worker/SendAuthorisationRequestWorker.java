package com.disp.automation.worker;

import com.disp.automation.service.SendAuthorisationRequestService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendAuthorisationRequestWorker {

    private static final Logger logger = LoggerFactory.getLogger(SendAuthorisationRequestWorker.class);

    private final SendAuthorisationRequestService sendAuthorisationRequestService;

    public SendAuthorisationRequestWorker(SendAuthorisationRequestService sendAuthorisationRequestService) {
        this.sendAuthorisationRequestService = sendAuthorisationRequestService;
    }

    @JobWorker(type = "sendAuthorisationRequest", autoComplete = false)
    public void handleSendAuthorisationRequest(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — sending authorisation request. Job key: {}", job.getKey());

        String orderId = vars.get("orderId") != null ? vars.get("orderId").toString() : null;

        if (orderId == null) {
            logger.error("orderId is null — cannot send authorisation request");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        try {
            sendAuthorisationRequestService.sendAuthorisationRequest(orderId, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("Authorisation request sent successfully for orderId: {}", orderId);

        } catch (Exception e) {
            logger.error("sendAuthorisationRequest failed for orderId {}: {}", orderId, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}