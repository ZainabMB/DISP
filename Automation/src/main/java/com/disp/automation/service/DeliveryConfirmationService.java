package com.disp.automation.service;

import com.disp.automation.entity.SaleOrder;
import com.disp.automation.repository.SaleOrderRepository;
import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeliveryConfirmationService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryConfirmationService.class);

    private final CamundaClient camundaClient;
    private final SaleOrderRepository saleOrderRepository;

    public DeliveryConfirmationService(CamundaClient camundaClient,
                                       SaleOrderRepository saleOrderRepository) {
        this.camundaClient = camundaClient;
        this.saleOrderRepository = saleOrderRepository;
    }

    public void sendDeliveryCompleted(String orderId, Map<String, Object> vars) {

        // Update status to DELIVERED in DB
        saleOrderRepository.findById(orderId).ifPresentOrElse(order -> {
            order.setStatus(SaleOrder.SaleStatus.DELIVERED);
            saleOrderRepository.save(order);
            logger.info("Order {} status updated to DELIVERED", orderId);
        }, () -> {
            logger.warn("Order {} not found — status not updated", orderId);
        });

        // Publish message to continue the process
        camundaClient
                .newPublishMessageCommand()
                .messageName("deliveryCompleted")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();

        logger.info("deliveryCompleted message published for orderId: {}", orderId);
    }
}