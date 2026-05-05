package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendApplicationDecisionService {

    private final CamundaClient camundaClient;

    public SendApplicationDecisionService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendApplicationDecision(String applicationId, Map<String, Object> vars) {
        camundaClient
                .newPublishMessageCommand()
                .messageName("ApplicationDecisionReceived")
                .correlationKey(applicationId)
                .variables(vars)
                .send()
                .join();
    }
}