package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.repository.HireOrderRepository;
import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class NotifyHireToolsReturnedService {

    private final CamundaClient camundaClient;
    private final HireOrderRepository hireOrderRepository;

    public NotifyHireToolsReturnedService(CamundaClient camundaClient,
                                          HireOrderRepository hireOrderRepository) {
        this.camundaClient = camundaClient;
        this.hireOrderRepository = hireOrderRepository;
    }

    public void sendHireToolsReturned(String orderId, Map<String, Object> vars) {

        // Load order
        HireOrder order = hireOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("HireOrder not found: " + orderId));

        // Update status + actual return date
        order.setStatus(HireOrder.HireStatus.RETURNED);
        order.setActualReturn(LocalDate.now());

        hireOrderRepository.save(order);

        // Publish BPMN message
        camundaClient
                .newPublishMessageCommand()
                .messageName("hireToolsReturned")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
