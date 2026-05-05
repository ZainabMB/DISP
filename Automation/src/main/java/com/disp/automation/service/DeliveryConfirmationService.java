package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeliveryConfirmationService {

    private final CamundaClient camundaClient;

    public DeliveryConfirmationService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendDeliveryCompleted(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("deliveryCompleted")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}

