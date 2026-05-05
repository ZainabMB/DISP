package com.disp.automation.worker;

import com.disp.automation.entity.CreditPlan;
import com.disp.automation.service.CreateCreditPlanService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateCreditPlanWorker {

    private static final Logger logger = LoggerFactory.getLogger(CreateCreditPlanWorker.class);

    private final CreateCreditPlanService createCreditPlanService;

    public CreateCreditPlanWorker(CreateCreditPlanService createCreditPlanService) {
        this.createCreditPlanService = createCreditPlanService;
    }

    @JobWorker(type = "createCreditPlan", autoComplete = false)
    public void handleCreateCreditPlan(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — creating credit plan. Job key: {}", job.getKey());
        logger.info("Variables received: {}", vars);

        CreditPlan plan = createCreditPlanService.createPlan(vars);

        logger.info("Credit plan created successfully with ID: {}", plan.getMemberId());

        client.newCompleteCommand(job.getKey())
                .send()
                .join();
    }
}
