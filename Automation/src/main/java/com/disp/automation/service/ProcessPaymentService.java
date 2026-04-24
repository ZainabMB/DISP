package com.disp.automation.service;

import com.disp.automation.entity.Tool;
import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProcessPaymentService {

    private final ToolRepository toolRepository;

    //insert tool repository to look up real prices
    public ProcessPaymentService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    //validate payment & verify order total against database price
    public boolean processPayment(double orderTotal, String paymentMethod,
                                  String customerName, String cardNumber,
                                  String cardExpiry, String cardCVV) {

        if (paymentMethod.equals("Card")) {
            //check card fields are not empty
            return orderTotal > 0
                    && !customerName.isEmpty()
                    && !cardNumber.isEmpty()
                    && !cardExpiry.isEmpty()
                    && !cardCVV.isEmpty();

        } else if (paymentMethod.equals("Cash")) {
            //cash only needs a valid order total
            return orderTotal > 0;

        } else {
            //unknown payment method
            return false;
        }
    }

    //look up item price from database
    public Double getPriceFromDatabase(String toolName) {
        Optional<Tool> tool = toolRepository.findByToolName(toolName);
        return tool.map(Tool::getPrice).orElse(0.0);
    }
}
