package com.disp.automation.repository;

import com.disp.automation.entity.CreditPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditPlanRepository extends JpaRepository<CreditPlan, Long> {

    // Check if a member already has an active credit plan
    boolean existsByMemberIdAndActiveTrue(Long memberId);

    // Optional: find a plan by applicationId
    Optional<CreditPlan> findByApplicationId(String applicationId);
}
