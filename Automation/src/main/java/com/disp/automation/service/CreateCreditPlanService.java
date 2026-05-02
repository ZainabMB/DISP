package com.disp.automation.service;

import com.disp.automation.entity.CreditPlan;
import com.disp.automation.repository.CreditPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class CreateCreditPlanService {

    private final CreditPlanRepository creditPlanRepository;

    public CreateCreditPlanService(CreditPlanRepository creditPlanRepository) {
        this.creditPlanRepository = creditPlanRepository;
    }

    public CreditPlan createPlan(Map<String, Object> vars) {

        Long memberId = Long.valueOf(vars.get("memberId").toString());
        String applicationId = vars.get("applicationId").toString();
        Integer durationMonths = Integer.valueOf(vars.get("installmentPlan").toString());
        Double totalPrice = Double.valueOf(vars.get("planPrice").toString());

        Double monthlyPayment = totalPrice / durationMonths;

        CreditPlan plan = new CreditPlan();
        plan.setMemberId(memberId);
        plan.setApplicationId(applicationId);
        plan.setDurationMonths(durationMonths);
        plan.setTotalPrice(totalPrice);
        plan.setMonthlyPayment(monthlyPayment);
        plan.setStartDate(LocalDate.now());
        plan.setActive(true);

        return creditPlanRepository.save(plan);
    }
}
