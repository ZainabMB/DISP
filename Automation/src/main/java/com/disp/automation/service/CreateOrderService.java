package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.entity.SaleOrder;
import com.disp.automation.entity.Tool;
import com.disp.automation.repository.HireOrderRepository;
import com.disp.automation.repository.SaleOrderRepository;
import com.disp.automation.repository.ToolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateOrderService {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

    private final ToolRepository toolRepository;
    private final HireOrderRepository hireOrderRepository;
    private final SaleOrderRepository saleOrderRepository;

    public CreateOrderService(ToolRepository toolRepository,
                              HireOrderRepository hireOrderRepository,
                              SaleOrderRepository saleOrderRepository) {
        this.toolRepository = toolRepository;
        this.hireOrderRepository = hireOrderRepository;
        this.saleOrderRepository = saleOrderRepository;
    }

    public String createOrder(Map<String, Object> vars) {

        String toolType            = vars.getOrDefault("toolType", "").toString();
        String toolName            = vars.getOrDefault("toolName", "").toString();
        String paymentMethodRaw    = vars.getOrDefault("paymentMethod", "unknown").toString();
        String distributionTypeRaw = vars.getOrDefault("distributionType", "pickup").toString();

        double totalPrice = Double.parseDouble(vars.getOrDefault("totalPrice", 0).toString());
        int quantity      = Integer.parseInt(vars.getOrDefault("quantity", 1).toString());

        Object memberRaw = vars.get("membershipNumber");
        Long memberId    = memberRaw != null ? Long.valueOf(memberRaw.toString()) : null;

        Long toolId = toolRepository.findByToolName(toolName)
                .map(Tool::getToolId)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        String orderId = UUID.randomUUID().toString();

        if (toolType.equalsIgnoreCase("HIRE")) {
            HireOrder order = new HireOrder();
            order.setOrderId(orderId);
            order.setMemberId(memberId);
            order.setToolId(toolId);
            order.setQuantity(quantity);
            order.setTotalPrice(totalPrice);
            order.setPaymentMethod(HireOrder.PaymentMethod.valueOf(paymentMethodRaw.toUpperCase()));
            order.setDistributionType(HireOrder.DistributionType.valueOf(distributionTypeRaw.toUpperCase()));
            order.setOrderDate(LocalDate.now());
            order.setStatus(HireOrder.HireStatus.PENDING);
            hireOrderRepository.save(order);
            logger.info("Hire order created — orderId: {}, toolId: {}, memberId: {}", orderId, toolId, memberId);

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
            logger.info("Sale order created — orderId: {}, toolId: {}, memberId: {}", orderId, toolId, memberId);

        } else {
            throw new IllegalArgumentException("Unknown toolType: " + toolType);
        }

        return orderId;
    }
}