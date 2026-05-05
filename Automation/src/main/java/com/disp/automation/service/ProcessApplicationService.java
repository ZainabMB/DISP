package com.disp.automation.service;

import com.disp.automation.entity.CreditPlan;
import com.disp.automation.repository.CreditPlanRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcessApplicationService {

    private final CreditPlanRepository creditPlanRepository;

    public ProcessApplicationService(CreditPlanRepository creditPlanRepository) {
        this.creditPlanRepository = creditPlanRepository;
    }

    public boolean hasActiveCreditPlan(Long memberId) {
        if (memberId == null) {
            return false;
        }
        return creditPlanRepository.existsByMemberIdAndStatus(memberId, CreditPlan.Status.ACTIVE);
    }
}
