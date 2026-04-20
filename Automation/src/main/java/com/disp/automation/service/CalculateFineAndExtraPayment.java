package com.disp.automation.service;

import io.camunda.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CalculateFineAndExtraPayment {
    @JobWorker(type = "calculateFine")
    public void handleCalculateFine(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap(); //gets all the variables inside camunda.
        Map<String, Object> result = new HashMap<>(); //creates a new empty bag where we put the answers to send back to Camunda at the end.

        // rentalEnd comes from the activate rental agreement form
        String rentalEndStr = (String) vars.get("rentalEnd"); //reaches into the envelope and pulls out specifically the rentalEnd variable
        LocalDate rentalEnd = LocalDate.parse(rentalEndStr); //convert to date
        LocalDate today = LocalDate.now(); //gets today's date

        // How many days past the rental end date
        long daysLate = ChronoUnit.DAYS.between(rentalEnd, today); //calculates the difference between the rental end date and today.

        double fineAmount = 0.0;
        if (daysLate > 0) { //if customer were late more than the amount of rentalEnd, they will get fined
            fineAmount = daysLate * 5.00; // £5 per day overdue
        }

        result.put("daysLate", daysLate); //how many days late the customer is
        result.put("fineAmount", fineAmount); //the fine amount

        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }

    @JobWorker(type = "calculateExtraPayment")
    public void handleCalculateExtraPayment(final JobClient client, final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();
        Map<String, Object> result = new HashMap<>();

        // damageNoted comes from the Receive Tools user task form
        boolean damageNoted = (boolean) vars.get("damageNoted");

        double totalPayment = 0.0;
        if (damageNoted) {
            totalPayment = 50.00; // fixed damage charge, adjust as needed
        }
        // if no damage → totalPayment stays 0.0

        result.put("totalPayment", totalPayment);

        client.newCompleteCommand(job.getKey())
                .variables(result)
                .send()
                .join();
    }
}