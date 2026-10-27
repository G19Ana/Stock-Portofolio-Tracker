package com.portfolio.service;

import com.portfolio.dto.AlertReq;
import com.portfolio.dto.Quote;
import com.portfolio.entity.Alert;
import com.portfolio.repo.AlertRepo;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class AlertService {

    private final AlertRepo repo;
    private final MarketDataService market;
    private final JavaMailSender mailSender;

    public AlertService(AlertRepo repo, MarketDataService market, JavaMailSender mailSender) {
        this.repo = repo;
        this.market = market;
        this.mailSender = mailSender;
    }

    public Alert create(AlertReq req) {
        return repo.save(new Alert(req.symbol(), req.targetPrice(), req.direction(), req.email()));
    }

    public List<Alert> all() {
        return repo.findAll();
    }

    public void remove(Long id) {
        repo.deleteById(id);
    }

    @Scheduled(fixedRate = 300000)
    public void scanAlerts() {
        List<Alert> active = repo.findByActiveTrue();
        if (active.isEmpty()) return;

        active.forEach(a -> {
            Quote q = market.fetchQuote(a.getSymbol());
            if (q.price().compareTo(java.math.BigDecimal.ZERO) <= 0) return;

            boolean triggered = (a.getDirection() == Alert.Direction.ABOVE && q.price().compareTo(a.getTargetPrice()) >= 0)
                    || (a.getDirection() == Alert.Direction.BELOW && q.price().compareTo(a.getTargetPrice()) <= 0);

            if (!triggered) return;

            dispatchEmail(a, q);
            a.setActive(false);
            repo.save(a);
        });
    }

    private void dispatchEmail(Alert alert, Quote quote) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(alert.getEmail());
        msg.setSubject("Price Alert: " + alert.getSymbol());
        msg.setText(String.format("Alert triggered for %s. Target: %s, Current: %s",
                alert.getSymbol(), alert.getTargetPrice(), quote.price()));
        mailSender.send(msg);
    }
}
