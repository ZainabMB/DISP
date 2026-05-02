package com.disp.automation.service;

import com.disp.automation.repository.CreditApplicationRepository;
import com.disp.automation.repository.CreditPlanRepository;
import org.springframework.stereotype.Service;

@Service
public class CreditApplicationService {

    private final CreditPlanRepository creditPlanRepository;

    public CreditApplicationService(CreditPlanRepository creditPlanRepository) {
        this.creditPlanRepository = creditPlanRepository;
    }

    public boolean hasActiveCreditPlan(Long memberId) {
        if (memberId == null) {
            return false;
        }
        return creditPlanRepository.existsByMemberIdAndActiveTrue(memberId);
    }
}
