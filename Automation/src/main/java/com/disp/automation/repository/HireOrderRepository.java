package com.disp.automation.repository;

import com.disp.automation.entity.HireOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HireOrderRepository extends JpaRepository<HireOrder, String> {}

