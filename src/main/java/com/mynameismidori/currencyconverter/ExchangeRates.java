package com.mynameismidori.currencyconverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class ExchangeRates {

    private long timestamp;
    private Map<String, BigDecimal> rates;
    private String baseCurrency;

    public int roundMode = BigDecimal.ROUND_HALF_DOWN;
    public int scale = 12;

    public ExchangeRates(long timestamp, Map<String, BigDecimal> rates, String baseCurrency) {
        this.timestamp = timestamp;
        this.rates = rates;
        this.baseCurrency = baseCurrency;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public BigDecimal getExchangeRate(String sourceCurrencyCode, String targetCurrencyCode) {
        BigDecimal sourceRate = getExchangeRate(sourceCurrencyCode);
        BigDecimal targetRate = getExchangeRate(targetCurrencyCode);

        return sourceRate.divide(targetRate, scale, roundMode);
    }

    public BigDecimal getExchangeRate(String targetCurrencyCode) {
        return rates.get(targetCurrencyCode);
    }

    public void setExchangeRate(String sourceCurrencyCode, String targetCurrencyCode, BigDecimal rate) {

        if (!targetCurrencyCode.equals(baseCurrency)) {
            BigDecimal targetRate = getExchangeRate(targetCurrencyCode);
            BigDecimal newRate = targetRate.divide(rate, scale, roundMode);

            rates.put(sourceCurrencyCode, newRate);
        } else {
            setExchangeRate(sourceCurrencyCode, rate);
        }
    }

    public void setExchangeRate(String sourceCurrencyCode, BigDecimal rate) {
        rates.put(sourceCurrencyCode, rate);
    }

    public Set<String> listAvailableCurrencies() {
        return rates.keySet();
    }

    public boolean reload() {
        return true;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String newBaseCurrency) {
        BigDecimal newBaseCurrencyRate = getExchangeRate(newBaseCurrency);

        for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
            BigDecimal oldValue = entry.getValue();
            BigDecimal newValue = getExchangeRate(entry.getKey(), newBaseCurrency);
            rates.put(entry.getKey(), newValue);
        }
        rates.put(baseCurrency, newBaseCurrencyRate);
        rates.put(newBaseCurrency, BigDecimal.valueOf(1.0d));
        baseCurrency = newBaseCurrency;
    }
}
