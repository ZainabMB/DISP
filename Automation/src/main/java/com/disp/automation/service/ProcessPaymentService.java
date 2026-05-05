package com.disp.automation.service;

import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class ProcessPaymentService {

    private final ToolRepository toolRepository;

    public ProcessPaymentService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public Map<String, Object> processPayment(Map<String, Object> vars) {

        String paymentMethodRaw = vars.getOrDefault("paymentMethod", "unknown").toString();
        String cardholderName   = vars.getOrDefault("cardholderName", "").toString();
        String cardNumber       = vars.getOrDefault("cardNumber", "").toString();
        String expiryDate       = vars.getOrDefault("expiryDate", "").toString();
        String cvv              = vars.getOrDefault("cvv", "").toString();

        double totalPrice = Double.parseDouble(vars.getOrDefault("totalPrice", 0).toString());

        boolean paymentCompleted = false;

        if (paymentMethodRaw.equalsIgnoreCase("CARD")) {
            paymentCompleted = totalPrice > 0
                    && !cardholderName.isEmpty()
                    && !cardNumber.isEmpty()
                    && !expiryDate.isEmpty()
                    && !cvv.isEmpty()
                    && isCardNotExpired(expiryDate);

        } else if (paymentMethodRaw.equalsIgnoreCase("CASH")) {
            paymentCompleted = totalPrice > 0;
        }

        return Map.of(
                "paymentCompleted", paymentCompleted,
                "paymentMethod", paymentMethodRaw,
                "amountPaid", totalPrice
        );
    }

    private boolean isCardNotExpired(String expiryDate) {
        try {
            YearMonth expiry = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("MM/yy"));
            return !expiry.isBefore(YearMonth.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}