package com.disp.automation.repository;

import com.disp.automation.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByToolType(String toolType);
    List<Tool> findByToolTypeAndQuantityGreaterThan(String toolType, int quantity);
    Optional<Tool> findByToolName(String toolName);
}