package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotifyHireToolsReceivedService {

    private final CamundaClient camundaClient;

    public NotifyHireToolsReceivedService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendHireToolsReceived(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("hireToolsReceived")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
