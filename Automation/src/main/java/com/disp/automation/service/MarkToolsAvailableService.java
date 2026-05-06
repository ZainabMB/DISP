package com.disp.automation.service;

import com.disp.automation.entity.ToolInstance;
import com.disp.automation.entity.OrderToolInstance;
import com.disp.automation.repository.OrderToolInstanceRepository;
import com.disp.automation.repository.ToolInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MarkToolsAvailableService {

    private static final Logger logger = LoggerFactory.getLogger(MarkToolsAvailableService.class);

    private final ToolInstanceRepository toolInstanceRepository;
    private final OrderToolInstanceRepository orderToolInstanceRepository;

    public MarkToolsAvailableService(ToolInstanceRepository toolInstanceRepository,
                                     OrderToolInstanceRepository orderToolInstanceRepository) {
        this.toolInstanceRepository = toolInstanceRepository;
        this.orderToolInstanceRepository = orderToolInstanceRepository;
    }

    public void markToolsAvailable(String orderId, Map<String, Object> vars) {

        String maintenanceOption = vars.get("maintenanceOption") != null
                ? vars.get("maintenanceOption").toString() : "";

        ToolInstance.ToolInstanceStatus newStatus = maintenanceOption.equalsIgnoreCase("decommissioned")
                ? ToolInstance.ToolInstanceStatus.RETIRED
                : ToolInstance.ToolInstanceStatus.AVAILABLE;

        List<OrderToolInstance> associations = orderToolInstanceRepository.findByOrderId(orderId);

        if (associations.isEmpty()) {
            logger.warn("No tool instances found for orderId: {}", orderId);
            return;
        }

        associations.forEach(association ->
                toolInstanceRepository.findById(association.getInstanceId()).ifPresentOrElse(instance -> {
                    instance.setStatus(newStatus);
                    toolInstanceRepository.save(instance);
                    logger.info("Tool instance {} set to {} for orderId: {}",
                            instance.getInstanceId(), newStatus, orderId);
                }, () -> logger.warn("Tool instance {} not found", association.getInstanceId()))
        );
    }
}