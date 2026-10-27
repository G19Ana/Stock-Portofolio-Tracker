package com.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfolio.dto.HistoryPoint;
import com.portfolio.dto.Quote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MarketDataService {

    private final RestClient client;
    private final String apiKey;

    public MarketDataService(@Value("${alphavantage.api.url}") String url,
                             @Value("${alphavantage.api.key}") String apiKey) {
        this.client = RestClient.builder().baseUrl(url).build();
        this.apiKey = apiKey;
    }

    @Cacheable(value = "quotes", key = "#sym.toUpperCase()")
    public Quote fetchQuote(String sym) {
        JsonNode root = client.get()
                .uri(b -> b.queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", sym)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (root == null) return new Quote(sym, BigDecimal.ZERO, BigDecimal.ZERO);

        JsonNode gq = root.path("Global Quote");
        if (gq.isMissingNode() || !gq.has("05. price")) {
            return new Quote(sym, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal price = new BigDecimal(gq.path("05. price").asText("0"));
        BigDecimal change = new BigDecimal(gq.path("09. change").asText("0"));
        return new Quote(sym.toUpperCase(), price, change);
    }

    @Cacheable(value = "history", key = "#sym.toUpperCase()")
    public List<HistoryPoint> fetchHistory(String sym) {
        JsonNode root = client.get()
                .uri(b -> b.queryParam("function", "TIME_SERIES_DAILY")
                        .queryParam("symbol", sym)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (root == null) return List.of();

        JsonNode ts = root.path("Time Series (Daily)");
        if (ts.isMissingNode()) return List.of();

        List<HistoryPoint> pts = new ArrayList<>();
        ts.fieldNames().forEachRemaining(date -> {
            BigDecimal close = new BigDecimal(ts.get(date).path("4. close").asText("0"));
            pts.add(new HistoryPoint(date, close));
        });

        return pts.stream()
                .sorted(Comparator.comparing(HistoryPoint::date))
                .toList();
    }
}
