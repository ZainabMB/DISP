package com.disp.automation.service;

import com.disp.automation.entity.Tool;
import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckAvailabilityService {

    private final ToolRepository toolRepository;

    public CheckAvailabilityService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public List<Map<String, Object>> getToolsByType(String toolType) {
        List<Tool> tools = toolRepository.findByToolTypeAndQuantityGreaterThan(toolType, 0);
        return tools.stream()
                .map(tool -> {
                    Map<String, Object> toolMap = new HashMap<>();
                    toolMap.put("label", tool.getToolName());
                    toolMap.put("value", tool.getToolName());
                    toolMap.put("price", tool.getPrice());
                    toolMap.put("quantity", tool.getQuantity());
                    return toolMap;
                })
                .collect(Collectors.toList());
    }
    public boolean isQuantityAvailable(String toolName, int requestedQuantity) {
        List<Tool> tools = toolRepository.findByToolType(toolName);
        if (tools.isEmpty()) return false;
        Tool tool = tools.get(0);
        return requestedQuantity >= 1 && requestedQuantity <= tool.getQuantity();
    }

    //calculate price
    public Double calculateTotalPrice(String toolName, int quantity) {
        return toolRepository.findByToolName(toolName)
                .map(tool -> tool.getPrice() * quantity)
                .orElse(0.0);
    }
}