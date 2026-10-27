package com.portfolio.repo;

import com.portfolio.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepo extends JpaRepository<Alert, Long> {
    List<Alert> findByActiveTrue();
}
