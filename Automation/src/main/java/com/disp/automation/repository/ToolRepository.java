package com.disp.automation.repository;

import com.disp.automation.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByToolType(String toolType);
    @Query(value = """
    SELECT t.* FROM probuilds.tool t
    WHERE t.tool_type = :toolType
    AND (SELECT COUNT(*) FROM probuilds.tool_instance ti 
         WHERE ti.tool_id = t.tool_id 
         AND ti.status = 'AVAILABLE') > 0
    """, nativeQuery = true)
    List<Tool> findAvailableToolsByType(@Param("toolType") String toolType);
    Optional<Tool> findByToolName(String toolName);
}