package com.disp.automation.repository;

import com.disp.automation.entity.CreditPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditPlanRepository extends JpaRepository<CreditPlan, Long> {

    // Check if a member already has an ACTIVE credit plan
    boolean existsByMemberIdAndStatus(Long memberId, CreditPlan.Status status);

    // Optional: find a plan by applicationId
    Optional<CreditPlan> findByApplicationId(String applicationId);
//for finding expired status
    List<CreditPlan> findByStatusAndEndDateBefore(CreditPlan.Status status, LocalDate date);
}