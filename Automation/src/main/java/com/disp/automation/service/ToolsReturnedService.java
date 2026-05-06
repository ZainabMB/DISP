package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ToolsReturnedService {

    private static final Logger logger = LoggerFactory.getLogger(ToolsReturnedService.class);

    private final CamundaClient camundaClient;

    public ToolsReturnedService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendToolsReturned(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("ToolsReturned")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();

        logger.info("ToolsReturned message published for orderId: {}", orderId);
    }
}