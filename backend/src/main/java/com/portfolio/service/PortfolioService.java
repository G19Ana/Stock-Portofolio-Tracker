package com.portfolio.service;

import com.portfolio.dto.HoldingReq;
import com.portfolio.dto.HoldingView;
import com.portfolio.dto.PortfolioView;
import com.portfolio.dto.Quote;
import com.portfolio.entity.Holding;
import com.portfolio.repo.HoldingRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {

    private final HoldingRepo repo;
    private final MarketDataService market;

    public PortfolioService(HoldingRepo repo, MarketDataService market) {
        this.repo = repo;
        this.market = market;
    }

    public List<Holding> all() {
        return repo.findAll();
    }

    public Holding add(HoldingReq req) {
        return repo.save(new Holding(req.symbol(), req.shares(), req.buyPrice()));
    }

    public void remove(Long id) {
        repo.deleteById(id);
    }

    public PortfolioView summarize() {
        List<Holding> holdings = repo.findAll();
        if (holdings.isEmpty()) {
            return new PortfolioView(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        BigDecimal totalCost = holdings.stream()
                .map(h -> h.getShares().multiply(h.getBuyPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TmpHolding> priced = holdings.stream()
                .map(h -> {
                    Quote q = market.fetchQuote(h.getSymbol());
                    BigDecimal mktVal = h.getShares().multiply(q.price());
                    BigDecimal cost = h.getShares().multiply(h.getBuyPrice());
                    BigDecimal pnl = mktVal.subtract(cost);
                    BigDecimal pnlPct = cost.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                            : pnl.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    return new TmpHolding(h, q.price(), cost, mktVal, pnl, pnlPct);
                })
                .toList();

        BigDecimal totalVal = priced.stream()
                .map(TmpHolding::mktVal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPnl = totalVal.subtract(totalCost);
        BigDecimal totalPnlPct = totalCost.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : totalPnl.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        List<HoldingView> views = priced.stream()
                .map(t -> {
                    BigDecimal alloc = totalVal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                            : t.mktVal().divide(totalVal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    return new HoldingView(
                            t.h().getId(),
                            t.h().getSymbol(),
                            t.h().getShares(),
                            t.h().getBuyPrice(),
                            t.price(),
                            t.cost().setScale(2, RoundingMode.HALF_UP),
                            t.mktVal().setScale(2, RoundingMode.HALF_UP),
                            t.pnl().setScale(2, RoundingMode.HALF_UP),
                            t.pnlPct().setScale(2, RoundingMode.HALF_UP),
                            alloc.setScale(2, RoundingMode.HALF_UP)
                    );
                })
                .toList();

        return new PortfolioView(
                totalCost.setScale(2, RoundingMode.HALF_UP),
                totalVal.setScale(2, RoundingMode.HALF_UP),
                totalPnl.setScale(2, RoundingMode.HALF_UP),
                totalPnlPct.setScale(2, RoundingMode.HALF_UP),
                views
        );
    }

    private record TmpHolding(Holding h, BigDecimal price, BigDecimal cost, BigDecimal mktVal, BigDecimal pnl, BigDecimal pnlPct) {}
}
