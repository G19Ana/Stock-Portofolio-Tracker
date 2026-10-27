package com.portfolio.controller;

import com.portfolio.dto.AlertReq;
import com.portfolio.entity.Alert;
import com.portfolio.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Alert> getAlerts() {
        return alertService.all();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Alert createAlert(@RequestBody AlertReq req) {
        return alertService.create(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAlert(@PathVariable Long id) {
        alertService.remove(id);
    }
}
