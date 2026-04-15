package com.disp.automation.controller;

import io.camunda.client.CamundaClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ProcessController {

    private final CamundaClient camundaClient;

    public ProcessController(CamundaClient camundaClient) {
        this.camundaClient = camundaClient;
    }

    @PostMapping("/start")
    public String startProcess(@RequestBody Map<String, Object> variables) {

        camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("Process_dhz0be0")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        return "Process started!";
    }
}