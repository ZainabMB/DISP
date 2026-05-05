package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InformCustomerService {

    private final CamundaClient camundaClient;

    public InformCustomerService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendOrderReady(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("orderReady")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
