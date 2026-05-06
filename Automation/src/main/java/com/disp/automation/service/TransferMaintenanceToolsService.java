package com.disp.automation.service;

import com.disp.automation.entity.ToolInstance;
import com.disp.automation.repository.OrderToolInstanceRepository;
import com.disp.automation.repository.ToolInstanceRepository;
import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TransferMaintenanceToolsService {

    private static final Logger logger = LoggerFactory.getLogger(TransferMaintenanceToolsService.class);

    private final CamundaClient camundaClient;
    private final ToolInstanceRepository toolInstanceRepository;
    private final OrderToolInstanceRepository orderToolInstanceRepository;

    public TransferMaintenanceToolsService(CamundaClient camundaClient,
                                           ToolInstanceRepository toolInstanceRepository,
                                           OrderToolInstanceRepository orderToolInstanceRepository) {
        this.camundaClient = camundaClient;
        this.toolInstanceRepository = toolInstanceRepository;
        this.orderToolInstanceRepository = orderToolInstanceRepository;
    }

    public void transferToMaintenance(String orderId, Map<String, Object> vars) {

        // Find all tool instances linked to this order
        List<com.disp.automation.entity.OrderToolInstance> associations =
                orderToolInstanceRepository.findByOrderId(orderId);

        if (associations.isEmpty()) {
            logger.warn("No tool instances found for orderId: {}", orderId);
        }

        // Update each instance status to MAINTENANCE
        associations.forEach(association -> {
            toolInstanceRepository.findById(association.getInstanceId()).ifPresentOrElse(instance -> {
                instance.setStatus(ToolInstance.ToolInstanceStatus.MAINTENANCE);
                toolInstanceRepository.save(instance);
                logger.info("Tool instance {} set to MAINTENANCE for orderId: {}",
                        instance.getInstanceId(), orderId);
            }, () -> logger.warn("Tool instance {} not found", association.getInstanceId()));
        });

        // Publish message
        camundaClient
                .newPublishMessageCommand()
                .messageName("maintenanceToolsReceived")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();

        logger.info("maintenanceToolsReceived message published for orderId: {}", orderId);
    }
}