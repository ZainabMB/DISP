package com.disp.automation.service;

import com.disp.automation.entity.CreditPlan;
import com.disp.automation.repository.CreditPlanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public class CreateCreditPlanService {

    private final CreditPlanRepository creditPlanRepository;

    public CreateCreditPlanService(CreditPlanRepository creditPlanRepository) {
        this.creditPlanRepository = creditPlanRepository;
    }

    public CreditPlan createPlan(Map<String, Object> vars) {

        Object memberRaw = vars.get("membershipNumber") != null
                ? vars.get("membershipNumber")
                : vars.get("memberId");
        if (memberRaw == null) throw new IllegalArgumentException("memberId/membershipNumber is missing");

        String applicationId = vars.get("application_id") != null
                ? vars.get("application_id").toString()
                : vars.get("applicationId") != null
                ? vars.get("applicationId").toString()
                : null;
        if (applicationId == null) throw new IllegalArgumentException("application_id is missing");

        Object installmentRaw = vars.get("installment_plan");
        if (installmentRaw == null) throw new IllegalArgumentException("installment_plan is missing");

        Object totalPriceRaw = vars.get("totalPrice");
        if (totalPriceRaw == null) throw new IllegalArgumentException("totalPrice is missing");

        Long memberId = Long.valueOf(memberRaw.toString());
        Integer durationMonths = installmentRaw.toString().startsWith("6") ? 6 : 12;
        Double totalPrice = Double.valueOf(totalPriceRaw.toString());
        Double monthlyPayment = totalPrice / durationMonths;

        CreditPlan plan = new CreditPlan();
        plan.setApplicationId(applicationId);  // PK set here
        plan.setMemberId(memberId);
        plan.setDurationMonths(durationMonths);
        plan.setTotalPrice(totalPrice);
        plan.setMonthlyPayment(BigDecimal.valueOf(monthlyPayment));
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(LocalDate.now().plusMonths(durationMonths));
        plan.setStatus(CreditPlan.Status.ACTIVE);

        return creditPlanRepository.save(plan);
    }
    @Scheduled(cron = "0 0 0 * * *") // runs at midnight every day
    public void expireCreditPlans() {
        List<CreditPlan> expired = creditPlanRepository
                .findByStatusAndEndDateBefore(CreditPlan.Status.ACTIVE, LocalDate.now());
        expired.forEach(plan -> plan.setStatus(CreditPlan.Status.INACTIVE));
        creditPlanRepository.saveAll(expired);
    }
}
