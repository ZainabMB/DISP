package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendCreditApplicationService {

    private final CamundaClient camundaClient;

    public SendCreditApplicationService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendCreditApplication(String applicationId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("creditApplicationReceived")
                .correlationKey(applicationId)
                .variables(vars)
                .send()
                .join();
    }
}

