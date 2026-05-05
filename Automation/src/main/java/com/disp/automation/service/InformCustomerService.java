package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.entity.SaleOrder;
import com.disp.automation.repository.HireOrderRepository;
import com.disp.automation.repository.SaleOrderRepository;
import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InformCustomerService {

    private static final Logger logger = LoggerFactory.getLogger(InformCustomerService.class);

    private final CamundaClient camundaClient;
    private final HireOrderRepository hireOrderRepository;
    private final SaleOrderRepository saleOrderRepository;

    public InformCustomerService(CamundaClient camundaClient,
                                 HireOrderRepository hireOrderRepository,
                                 SaleOrderRepository saleOrderRepository) {
        this.camundaClient = camundaClient;
        this.hireOrderRepository = hireOrderRepository;
        this.saleOrderRepository = saleOrderRepository;
    }

    public void sendOrderReady(String orderId, Map<String, Object> vars) {

        String toolType = vars.getOrDefault("toolType", "").toString();

        // Update status to PICKED_UP based on toolType
        if (toolType.equalsIgnoreCase("HIRE")) {
            hireOrderRepository.findById(orderId).ifPresentOrElse(order -> {
                order.setStatus(HireOrder.HireStatus.PICKED_UP);
                hireOrderRepository.save(order);
                logger.info("Hire order {} status updated to PICKED_UP", orderId);
            }, () -> logger.warn("Hire order {} not found — status not updated", orderId));

        } else if (toolType.equalsIgnoreCase("SALE")) {
            saleOrderRepository.findById(orderId).ifPresentOrElse(order -> {
                order.setStatus(SaleOrder.SaleStatus.PICKED_UP);
                saleOrderRepository.save(order);
                logger.info("Sale order {} status updated to PICKED_UP", orderId);
            }, () -> logger.warn("Sale order {} not found — status not updated", orderId));
        }

        // Publish message to continue the process
        camundaClient
                .newPublishMessageCommand()
                .messageName("orderReady")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();

        logger.info("orderReady message published for orderId: {}", orderId);
    }
}