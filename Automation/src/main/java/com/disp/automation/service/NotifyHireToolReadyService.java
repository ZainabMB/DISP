package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotifyHireToolReadyService {

    private final CamundaClient camundaClient;

    public NotifyHireToolReadyService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendHireToolReady(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("hireToolReady")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
