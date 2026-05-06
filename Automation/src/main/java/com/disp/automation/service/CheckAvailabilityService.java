package com.disp.automation.service;

import com.disp.automation.entity.Tool;
import com.disp.automation.entity.ToolInstance;
import com.disp.automation.repository.ToolInstanceRepository;
import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckAvailabilityService {

    private final ToolRepository toolRepository;
    private final ToolInstanceRepository toolInstanceRepository;
    public CheckAvailabilityService(ToolRepository toolRepository, ToolInstanceRepository toolInstanceRepository) {
        this.toolRepository = toolRepository;
        this.toolInstanceRepository = toolInstanceRepository;
    }

    public List<Map<String, Object>> getToolsByType(String toolType) {
        List<Tool> tools = toolRepository.findAvailableToolsByType(toolType);
        return tools.stream()
                .map(tool -> {
                    int available = toolInstanceRepository.countByToolIdAndStatus(
                            tool.getToolId(), ToolInstance.ToolInstanceStatus.AVAILABLE
                    );
                    Map<String, Object> toolMap = new HashMap<>();
                    toolMap.put("label", tool.getToolName());
                    toolMap.put("value", tool.getToolName());
                    toolMap.put("price", tool.getPrice());
                    toolMap.put("quantity", available);
                    return toolMap;
                })
                .collect(Collectors.toList());
    }
    public boolean isQuantityAvailable(String toolName, int requestedQuantity) {
        return toolRepository.findByToolName(toolName)
                .map(tool -> {
                    int available = toolInstanceRepository.countByToolIdAndStatus(
                            tool.getToolId(), ToolInstance.ToolInstanceStatus.AVAILABLE
                    );
                    return requestedQuantity >= 1 && requestedQuantity <= available;
                })
                .orElse(false);
    }

    public Double calculateTotalPrice(String toolName, int quantity) {
        return toolRepository.findByToolName(toolName)
                .map(tool -> tool.getPrice() * quantity)
                .orElse(0.0);
    }
}