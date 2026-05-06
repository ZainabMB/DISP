package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendAuthorisationRequestService {

    private static final Logger logger = LoggerFactory.getLogger(SendAuthorisationRequestService.class);

    private final CamundaClient camundaClient;

    public SendAuthorisationRequestService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendAuthorisationRequest(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("AuthorisationRequestReceived")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();

        logger.info("AuthorisationRequestReceived message published for orderId: {}", orderId);
    }
}