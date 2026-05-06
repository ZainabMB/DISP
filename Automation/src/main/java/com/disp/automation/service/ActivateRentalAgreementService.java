package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.repository.HireOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class ActivateRentalAgreementService {

    private final HireOrderRepository hireOrderRepository;

    public ActivateRentalAgreementService(HireOrderRepository hireOrderRepository) {
        this.hireOrderRepository = hireOrderRepository;
    }

    public HireOrder activateRentalAgreement(String orderId, Map<String, Object> vars) {

        HireOrder hireOrder = hireOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Hire order not found: " + orderId));

        // Update status
        hireOrder.setStatus(HireOrder.HireStatus.PICKED_UP);

        // Set condition at issue
        Object conditionRaw = vars.get("conditionAtIssue");
        if (conditionRaw != null && !conditionRaw.toString().isEmpty()) {
            try {
                hireOrder.setConditionAtIssue(
                        HireOrder.ToolCondition.valueOf(conditionRaw.toString().toUpperCase())
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid condition value: " + conditionRaw);
            }
        }

        // Set condition notes
        Object notesRaw = vars.get("notesOnIssue");
        if (notesRaw != null) {
            hireOrder.setConditionNotes(notesRaw.toString());
        }

        // Set issue date
        Object issueDateRaw = vars.get("issueDate");
        if (issueDateRaw != null && !issueDateRaw.toString().isEmpty()) {
            hireOrder.setIssueDate(LocalDate.parse(issueDateRaw.toString()));
        }

        // Set expected return date
        Object expectedReturnRaw = vars.get("expectedReturnDate");
        if (expectedReturnRaw != null && !expectedReturnRaw.toString().isEmpty()) {
            hireOrder.setExpectedReturn(LocalDate.parse(expectedReturnRaw.toString()));
        }

        return hireOrderRepository.save(hireOrder);
    }
}