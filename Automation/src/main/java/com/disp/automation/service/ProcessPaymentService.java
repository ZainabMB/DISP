package com.disp.automation;

import org.springframework.stereotype.Service;

@Service
public class ProcessPaymentService {

    //validate and process payment based on method
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
}