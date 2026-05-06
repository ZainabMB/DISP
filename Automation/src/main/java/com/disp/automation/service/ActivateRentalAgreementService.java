package com.disp.automation.service;

import com.disp.automation.entity.HireOrder;
import com.disp.automation.repository.HireOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivateRentalAgreementService {

    private final HireOrderRepository hireOrderRepository;

    public ActivateRentalAgreementService(HireOrderRepository hireOrderRepository) {
        this.hireOrderRepository = hireOrderRepository;
    }

    public HireOrder activateRentalAgreement(String orderId) {

        HireOrder hireOrder = hireOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Hire order not found: " + orderId));

        // Update status
        hireOrder.setStatus(HireOrder.HireStatus.PICKED_UP);

        return hireOrderRepository.save(hireOrder);
    }
}
