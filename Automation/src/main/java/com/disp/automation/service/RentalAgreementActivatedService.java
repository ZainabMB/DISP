package com.disp.automation.service;

import io.camunda.client.CamundaClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RentalAgreementActivatedService {

    private final CamundaClient camundaClient;

    public RentalAgreementActivatedService(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    public void sendRentalAgreementActivated(String orderId, Map<String, Object> vars) {

        camundaClient
                .newPublishMessageCommand()
                .messageName("rentalAgreementActivated")
                .correlationKey(orderId)
                .variables(vars)
                .send()
                .join();
    }
}
