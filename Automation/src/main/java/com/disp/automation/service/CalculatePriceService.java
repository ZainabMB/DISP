package com.disp.automation.service;

import com.disp.automation.entity.Member;
import com.disp.automation.repository.MemberRepository;
import com.disp.automation.repository.ToolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalculatePriceService {

    private static final Logger logger = LoggerFactory.getLogger(CalculatePriceService.class);

    private static final int POINTS_PER_PERCENT = 100;
    private static final int MAX_DISCOUNT_PERCENT = 10;

    private final ToolRepository toolRepository;
    private final MemberRepository memberRepository;

    public CalculatePriceService(ToolRepository toolRepository, MemberRepository memberRepository) {
        this.toolRepository = toolRepository;
        this.memberRepository = memberRepository;
    }

    public Map<String, Object> calculatePrice(String toolType, String toolName, int quantity,
                                              String membershipNumber, boolean register_member, int hireDays, String distributionType) {Map<String, Object> result = new HashMap<>();

        double unitPrice = toolRepository.findByToolName(toolName)
                .map(tool -> tool.getPrice())
                .orElse(0.0);

        double totalPrice = unitPrice * quantity;

// Multiply by hireDays for hire orders
        if (toolType.equalsIgnoreCase("HIRE") && hireDays > 0) {
            totalPrice = unitPrice * quantity * hireDays;
            logger.info("Hire pricing — unitPrice: {}, quantity: {}, hireDays: {}, totalPrice: {}",
                    unitPrice, quantity, hireDays, Math.round(totalPrice));
        }

// Add £4 delivery fee
        if (distributionType != null && distributionType.equalsIgnoreCase("delivery")) {
            totalPrice += 4;
            result.put("deliveryFee", 4);
            logger.info("Delivery fee of £4 added — new total: {}", Math.round(totalPrice));
        } else {
            result.put("deliveryFee", 0);
        }

// Add £10 membership registration fee
        if (register_member) {
            totalPrice += 10;
            result.put("membershipFee", 10);
            logger.info("Membership registration fee of £10 added — new total: {}", Math.round(totalPrice));
        } else {
            result.put("membershipFee", 0);
        }

        result.put("totalPrice", Math.round(totalPrice));
        result.put("discountApplied", 0);
        result.put("pointsUsed", 0);
        result.put("discountAmount", 0);

        // If not a member, return base price (+ fee if registering)
        if (membershipNumber == null || membershipNumber.equals("no_member")) {
            logger.info("No existing member — totalPrice: {}", Math.round(totalPrice));
            return result;
        }

        // Parse memberId and look up loyalty points
        long memberId;
        try {
            memberId = Long.parseLong(membershipNumber);
        } catch (NumberFormatException e) {
            logger.warn("isMember value '{}' is not a valid memberId, returning base price", membershipNumber);
            return result;
        }

        Member member = memberRepository.findByMemberId(memberId).orElse(null);
        if (member == null || member.getLoyaltyPoints() == null || member.getLoyaltyPoints() == 0) {
            logger.info("Member {} has no loyalty points — totalPrice: {}", memberId, Math.round(totalPrice));
            return result;
        }

        int loyaltyPoints = member.getLoyaltyPoints();

        // Calculate discount: 100 points = 1%, max 10%
        int discountPercent = Math.min(loyaltyPoints / POINTS_PER_PERCENT, MAX_DISCOUNT_PERCENT);
        int pointsUsed = discountPercent * POINTS_PER_PERCENT;

        double discountAmount = totalPrice * discountPercent / 100.0;
        long discountedPrice = Math.round(totalPrice - discountAmount);

        logger.info("Member {} — points: {}, discount: {}%, pointsUsed: {}, totalPrice: {}",
                memberId, loyaltyPoints, discountPercent, pointsUsed, discountedPrice);

        result.put("totalPrice", discountedPrice);
        result.put("discountApplied", discountPercent);
        result.put("pointsUsed", pointsUsed);
        result.put("discountAmount", Math.round(discountAmount));

        return result;
    }
}