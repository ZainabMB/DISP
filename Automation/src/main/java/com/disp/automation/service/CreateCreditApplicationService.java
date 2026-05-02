package com.disp.automation.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateCreditApplicationService {

    public Map<String, Object> generateApplication(Map<String, Object> vars) {

        // Generate unique application ID
        String applicationId = UUID.randomUUID().toString();

        // Copy all existing variables
        Map<String, Object> result = new HashMap<>(vars);

        // Add the generated application ID
        result.put("applicationId", applicationId);

        return result;
    }
}
