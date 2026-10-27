package com.portfolio.repo;

import com.portfolio.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepo extends JpaRepository<Holding, Long> {}
