package com.disp.automation.repository;

import com.disp.automation.entity.ToolInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolInstanceRepository extends JpaRepository<ToolInstance, Long> {

    // count available instances for a tool
    int countByToolIdAndStatus(Long toolId, ToolInstance.ToolInstanceStatus status);

    // find available instances for a tool
    List<ToolInstance> findByToolIdAndStatus(Long toolId, ToolInstance.ToolInstanceStatus status);

    // find by serial number
    Optional<ToolInstance> findBySerialNumber(String serialNumber);
}