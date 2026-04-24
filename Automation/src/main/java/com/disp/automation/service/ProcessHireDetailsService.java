package com.disp.automation;

import org.springframework.stereotype.Service;

@Service
public class ProcessHireDetailsService {

    //validate that all required hire fields are present
    public boolean validateHireDetails(String customerName, String toolName,
                                       String hireStartDate, String hireEndDate,
                                       double depositAmount, String phone, String email) {

        //all fields must be filled and deposit must be greater than zero
        return !customerName.equals("unknown")
                && !toolName.equals("unknown")
                && !hireStartDate.isEmpty()
                && !hireEndDate.isEmpty()
                && !phone.isEmpty()
                && !email.isEmpty()
                && depositAmount > 0;
    }
}