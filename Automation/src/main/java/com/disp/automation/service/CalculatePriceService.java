package com.disp.automation.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class CalculatePriceService {

    //fake database bs i hate you zainab
    private static final Map<String, Double> TOOLS = new HashMap<>();

    static {
        TOOLS.put("Hammer",           8.00);
        TOOLS.put("Screwdriver",      4.50);
        TOOLS.put("Power Drill",     45.00);
        TOOLS.put("Cement Mixer",   250.00);
        TOOLS.put("Pressure Washer",150.00);
        TOOLS.put("Tile Cutter",    120.00);
        TOOLS.put("Heavy-Duty Drill",200.00);
        TOOLS.put("Scaffolding",    500.00);
    }

    //discount rate
    private static final double MEMBER_DISCOUNT_RATE = 0.10; // 10%

    public Map<String, Object> calculatePrice(String transactionType, String toolName,
                                              int quantity, boolean isMember) {
        Map<String, Object> result = new HashMap<>();

        Double unitPrice = TOOLS.get(toolName);

        // error handling if tool not found
        if (unitPrice == null) {
            result.put("priceCalculationError",   "Tool not found: " + toolName);
            result.put("priceCalculationSuccess", false);
            return result;
        }

        double baseTotal      = unitPrice * quantity;
        double discountAmount = isMember ? round(baseTotal * MEMBER_DISCOUNT_RATE) : 0.0;
        double finalPrice     = round(baseTotal - discountAmount);

        result.put("transactionType",         transactionType);
        result.put("toolName",                toolName);
        result.put("quantity",                quantity);
        result.put("unitPrice",               unitPrice);
        result.put("baseTotal",               round(baseTotal));
        result.put("isMember",                isMember);
        result.put("discountApplied",         isMember ? "Trade Card 10%" : "None");
        result.put("discountAmount",          discountAmount);
        result.put("finalPrice",              finalPrice);
        result.put("priceCalculationSuccess", true);
        result.put("priceBreakdown",
                toolName + " x" + quantity
                        + " @ £" + unitPrice
                        + " = £" + round(baseTotal)
                        + (isMember ? " | Member discount -£" + discountAmount : "")
                        + " | Total: £" + finalPrice);

        return result;
    }

    //calls calculateprice and isMember or not
    public Map<String, Object> calculatePrice(String transactionType, String toolName,
                                              int quantity, String ismember) {
        boolean isMember = "yes_member".equalsIgnoreCase(ismember);
        return calculatePrice(transactionType, toolName, quantity, isMember);
    }

    //check if tool exists
    public boolean toolExists(String toolName) {
        return TOOLS.containsKey(toolName);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}