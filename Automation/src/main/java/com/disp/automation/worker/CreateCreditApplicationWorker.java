package com.disp.automation.worker;

import com.disp.automation.service.CreateCreditApplicationService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateCreditApplicationWorker {

    private static final Logger logger = LoggerFactory.getLogger(CreateCreditApplicationWorker.class);

    private final CreateCreditApplicationService createCreditApplicationService;

    public CreateCreditApplicationWorker(CreateCreditApplicationService createCreditApplicationService) {
        this.createCreditApplicationService = createCreditApplicationService;
    }

    @JobWorker(type = "createCreditApplication", autoComplete = false)
    public void handleCreateCreditApplication(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — generating credit application. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        // Generate applicationId and prepare variables
        Map<String, Object> result = createCreditApplicationService.generateApplication(vars);

        logger.info("Generated applicationId: {}", result.get("applicationId"));

        // Complete the task and pass variables forward
        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }
}
