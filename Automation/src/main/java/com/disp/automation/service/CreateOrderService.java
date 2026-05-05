package com.disp.automation.service;

import com.disp.automation.entity.*;
import com.disp.automation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CreateOrderService {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderService.class);

    private final ToolRepository toolRepository;
    private final HireOrderRepository hireOrderRepository;
    private final SaleOrderRepository saleOrderRepository;
    private final ToolInstanceRepository toolInstanceRepository;
    private final OrderToolInstanceRepository orderToolInstanceRepository;

    public CreateOrderService(ToolRepository toolRepository,
                              HireOrderRepository hireOrderRepository,
                              SaleOrderRepository saleOrderRepository,
                              ToolInstanceRepository toolInstanceRepository, OrderToolInstanceRepository orderToolInstanceRepository) {
        this.toolRepository = toolRepository;
        this.hireOrderRepository = hireOrderRepository;
        this.saleOrderRepository = saleOrderRepository;
        this.toolInstanceRepository = toolInstanceRepository;
        this.orderToolInstanceRepository = orderToolInstanceRepository;
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

        // Fetch tool and validate toolType matches
        Tool tool = toolRepository.findByToolName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        if (!tool.getToolType().equalsIgnoreCase(toolType)) {
            throw new IllegalArgumentException(
                    "Tool " + toolName + " is not available for " + toolType
            );
        }

        Long toolId = tool.getToolId();

// Fetch available instances
        List<ToolInstance> availableInstances = toolInstanceRepository
                .findByToolIdAndStatus(toolId, ToolInstance.ToolInstanceStatus.AVAILABLE);

        if (availableInstances.size() < quantity) {
            throw new IllegalArgumentException(
                    "Not enough instances available for " + toolName +
                            " — requested: " + quantity + ", available: " + availableInstances.size()
            );
        }

// Generate orderId BEFORE the loop
        String orderId = UUID.randomUUID().toString();

// Mark the required number of instances as HIRED or SOLD
        ToolInstance.ToolInstanceStatus newStatus = toolType.equalsIgnoreCase("SALE")
                ? ToolInstance.ToolInstanceStatus.SOLD
                : ToolInstance.ToolInstanceStatus.HIRED;

        List<ToolInstance> instancesToUpdate = availableInstances.subList(0, quantity);

        instancesToUpdate.forEach(instance -> {
            instance.setStatus(newStatus);
            toolInstanceRepository.save(instance);

            OrderToolInstance association = new OrderToolInstance();
            association.setOrderId(orderId);
            association.setInstanceId(instance.getInstanceId());
            association.setOrderType(toolType.toUpperCase());
            orderToolInstanceRepository.save(association);

            logger.info("Tool instance {} linked to order {} — instanceId: {}",
                    toolName, orderId, instance.getInstanceId());
        });

// Then save the order below...

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
            logger.info("Hire order created — orderId: {}, toolId: {}, memberId: {}, quantity: {}",
                    orderId, toolId, memberId, quantity);

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
            logger.info("Sale order created — orderId: {}, toolId: {}, memberId: {}, quantity: {}",
                    orderId, toolId, memberId, quantity);

        } else {
            throw new IllegalArgumentException("Unknown toolType: " + toolType);
        }

        return orderId;
    }
}