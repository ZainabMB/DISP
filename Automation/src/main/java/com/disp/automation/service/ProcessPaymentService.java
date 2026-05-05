package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.entity.SaleOrder;
import com.disp.automation.entity.Tool;
import com.disp.automation.repository.HireOrderRepository;
import com.disp.automation.repository.SaleOrderRepository;
import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcessPaymentService {

    private final ToolRepository toolRepository;
    private final HireOrderRepository hireOrderRepository;
    private final SaleOrderRepository saleOrderRepository;

    public ProcessPaymentService(ToolRepository toolRepository,
                                 HireOrderRepository hireOrderRepository,
                                 SaleOrderRepository saleOrderRepository) {
        this.toolRepository = toolRepository;
        this.hireOrderRepository = hireOrderRepository;
        this.saleOrderRepository = saleOrderRepository;
    }
    private boolean isCardNotExpired(String expiryDate) {
        try {
            YearMonth expiry = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("MM/yy"));
            return !expiry.isBefore(YearMonth.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public Map<String, Object> processPayment(Map<String, Object> vars) {

        String paymentMethodRaw     = vars.getOrDefault("paymentMethod", "unknown").toString();
        String cardholderName       = vars.getOrDefault("cardholderName", "").toString();
        String cardNumber           = vars.getOrDefault("cardNumber", "").toString();
        String expiryDate           = vars.getOrDefault("expiryDate", "").toString();
        String cvv                  = vars.getOrDefault("cvv", "").toString();
        String toolType             = vars.getOrDefault("toolType", "").toString();
        String toolName             = vars.getOrDefault("toolName", "").toString();
        String distributionTypeRaw  = vars.getOrDefault("distributionType", "pickup").toString();

        double totalPrice   = Double.parseDouble(vars.getOrDefault("totalPrice", 0).toString());
        int quantity        = Integer.parseInt(vars.getOrDefault("quantity", 1).toString());

        Object memberRaw    = vars.get("membershipNumber");
        Long memberId       = memberRaw != null ? Long.valueOf(memberRaw.toString()) : null;

        // resolve toolId from toolName
        Long toolId = toolRepository.findByToolName(toolName)
                .map(Tool::getToolId)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        // parse enums
        HireOrder.PaymentMethod paymentMethod;
        try {
            paymentMethod = HireOrder.PaymentMethod.valueOf(paymentMethodRaw.toUpperCase());
        } catch (IllegalArgumentException e) {
            paymentMethod = null;
        }

        HireOrder.DistributionType distributionType;
        try {
            distributionType = HireOrder.DistributionType.valueOf(distributionTypeRaw.toUpperCase());
        } catch (IllegalArgumentException e) {
            distributionType = HireOrder.DistributionType.PICKUP;
        }

        // validate payment
        boolean paymentCompleted = false;
        if (paymentMethod == HireOrder.PaymentMethod.CARD) {
            paymentCompleted = totalPrice > 0
                    && !cardholderName.isEmpty()
                    && !cardNumber.isEmpty()
                    && !expiryDate.isEmpty()
                    && !cvv.isEmpty()
                    && isCardNotExpired(expiryDate);

        } else if (paymentMethod == HireOrder.PaymentMethod.CASH) {
            paymentCompleted = totalPrice > 0;
        }

        String orderId = null;

        if (paymentCompleted) {
            orderId = UUID.randomUUID().toString();

            if (toolType.equalsIgnoreCase("HIRE")) {
                HireOrder order = new HireOrder();
                order.setOrderId(orderId);
                order.setMemberId(memberId);
                order.setToolId(toolId);
                order.setQuantity(quantity);
                order.setTotalPrice(totalPrice);
                order.setPaymentMethod(paymentMethod);
                order.setDistributionType(distributionType);
                order.setOrderDate(LocalDate.now());
                order.setStatus(HireOrder.HireStatus.PENDING);
                hireOrderRepository.save(order);

            } else if (toolType.equalsIgnoreCase("SALE")) {
                SaleOrder order = new SaleOrder();
                order.setOrderId(orderId);
                order.setMemberId(memberId);
                order.setToolId(toolId);
                order.setQuantity(quantity);
                order.setTotalPrice(totalPrice);
                order.setPaymentMethod(SaleOrder.PaymentMethod.valueOf(paymentMethodRaw.toUpperCase()));
                order.setDistributionType(SaleOrder.DistributionType.valueOf(distributionTypeRaw.toUpperCase()));
                order.setOrderDate(LocalDate.now());
                order.setStatus(SaleOrder.SaleStatus.PENDING);
                saleOrderRepository.save(order);
            }
        }

        return Map.of(
                "paymentCompleted", paymentCompleted,
                "paymentMethod", paymentMethodRaw,
                "amountPaid", totalPrice,
                "orderId", orderId != null ? orderId : ""
        );
    }
}