package com.portfolio.controller;

import com.portfolio.dto.HistoryPoint;
import com.portfolio.dto.HoldingReq;
import com.portfolio.dto.PortfolioView;
import com.portfolio.entity.Holding;
import com.portfolio.service.MarketDataService;
import com.portfolio.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolio;
    private final MarketDataService market;

    public PortfolioController(PortfolioService portfolio, MarketDataService market) {
        this.portfolio = portfolio;
        this.market = market;
    }

    @GetMapping
    public PortfolioView getSummary() {
        return portfolio.summarize();
    }

    @PostMapping("/holdings")
    @ResponseStatus(HttpStatus.CREATED)
    public Holding addHolding(@RequestBody HoldingReq req) {
        return portfolio.add(req);
    }

    @DeleteMapping("/holdings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeHolding(@PathVariable Long id) {
        portfolio.remove(id);
    }

    @GetMapping("/history/{symbol}")
    public List<HistoryPoint> getHistory(@PathVariable String symbol) {
        return market.fetchHistory(symbol);
    }
}
