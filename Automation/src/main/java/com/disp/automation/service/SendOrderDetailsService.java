package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendOrderDetailsService {

    private final CamundaClient camundaClient;

    public SendOrderDetailsService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendOrderDetails(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("orderDetails")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
