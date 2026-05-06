package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendRepairDecisionService {

    private static final Logger logger = LoggerFactory.getLogger(SendRepairDecisionService.class);

    private final CamundaClient camundaClient;

    public SendRepairDecisionService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendRepairDecision(String serialNumber, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("ReceiveAuthorisationDecision")
                .correlationKey(serialNumber)
                .variables(vars)
                .send()
                .join();

        logger.info("ReceiveAuthorisationDecision message published for serialNumber: {}", serialNumber);
    }
}