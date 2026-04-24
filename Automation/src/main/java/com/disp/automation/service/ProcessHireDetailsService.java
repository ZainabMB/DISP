package com.disp.automation.service;

import com.disp.automation.entity.Tool;
import com.disp.automation.repository.ToolRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProcessHireDetailsService {

    private final ToolRepository toolRepository;

    //insert tool repository to check tool exists and is available for hire
    public ProcessHireDetailsService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    //validate hire details & check tool exists in database
    public boolean validateHireDetails(String customerName, String toolName,
                                       String hireStartDate, String hireEndDate,
                                       double depositAmount, String phone, String email) {

        //check tool exists in the database
        Optional<Tool> tool = toolRepository.findByToolName(toolName);
        boolean toolExists = tool.isPresent();

        //all fields must be present, deposit > 0, and tool must exist in database
        return !customerName.equals("unknown")
                && !toolName.equals("unknown")
                && !hireStartDate.isEmpty()
                && !hireEndDate.isEmpty()
                && !phone.isEmpty()
                && !email.isEmpty()
                && depositAmount > 0
                && toolExists;
    }
}
