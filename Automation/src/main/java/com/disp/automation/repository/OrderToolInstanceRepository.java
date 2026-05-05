package com.disp.automation.repository;

import com.disp.automation.entity.OrderToolInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderToolInstanceRepository extends JpaRepository<OrderToolInstance, Long> {
    List<OrderToolInstance> findByOrderId(String orderId);
    List<OrderToolInstance> findByInstanceId(Long instanceId);
}