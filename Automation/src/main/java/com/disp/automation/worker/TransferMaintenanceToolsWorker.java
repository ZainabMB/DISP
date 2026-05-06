package com.disp.automation.worker;

import com.disp.automation.service.TransferMaintenanceToolsService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransferMaintenanceToolsWorker {

    private static final Logger logger = LoggerFactory.getLogger(TransferMaintenanceToolsWorker.class);

    private final TransferMaintenanceToolsService transferMaintenanceToolsService;

    public TransferMaintenanceToolsWorker(TransferMaintenanceToolsService transferMaintenanceToolsService) {
        this.transferMaintenanceToolsService = transferMaintenanceToolsService;
    }

    @JobWorker(type = "transferMaintenanceTools", autoComplete = false)
    public void handleTransferMaintenanceTools(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        logger.info("Worker triggered — transferring tools to maintenance. Job key: {}", job.getKey());

        String orderId = vars.get("orderId") != null ? vars.get("orderId").toString() : null;

        if (orderId == null) {
            logger.error("orderId is null — cannot transfer tools to maintenance");
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("orderId is missing")
                    .send()
                    .join();
            return;
        }

        try {
            transferMaintenanceToolsService.transferToMaintenance(orderId, vars);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            logger.info("Tools transferred to maintenance successfully for orderId: {}", orderId);

        } catch (Exception e) {
            logger.error("transferMaintenanceTools failed for orderId {}: {}", orderId, e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}