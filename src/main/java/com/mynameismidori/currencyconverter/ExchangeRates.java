package com.mynameismidori.currencyconverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class ExchangeRates {

    private long timestamp;
    private Map<String, BigDecimal> rates;
    private String baseCurrency;
    private boolean inverse = false;

    public int roundMode = BigDecimal.ROUND_HALF_UP;
    public int scale = 12;

    public ExchangeRates(long timestamp, Map<String, BigDecimal> rates, String baseCurrency) {
        this.timestamp = timestamp;
        this.rates = rates;
        this.rates.put(baseCurrency, BigDecimal.valueOf(1.0d));
        this.baseCurrency = baseCurrency;
    }

    public ExchangeRates(long timestamp, Map<String, BigDecimal> rates, String baseCurrency, boolean inverse) {
        this.timestamp = timestamp;
        this.rates = rates;
        this.rates.put(baseCurrency, BigDecimal.valueOf(1.0d));
        this.inverse = inverse;
        this.baseCurrency = baseCurrency;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public BigDecimal getExchangeRate(String sourceCurrencyCode, String targetCurrencyCode) {
        BigDecimal sourceRate = getExchangeRate(sourceCurrencyCode);
        BigDecimal targetRate = getExchangeRate(targetCurrencyCode);

        BigDecimal result = targetRate.divide(sourceRate, scale, roundMode);
        if (inverse) {
            result = sourceRate.divide(targetRate, scale, roundMode);
        }
        return result;
    }

    public BigDecimal getExchangeRate(String targetCurrencyCode) {
        if (inverse) {
            return BigDecimal.valueOf(1.0d).divide(rates.get(targetCurrencyCode), scale, roundMode);
        }
        return rates.get(targetCurrencyCode);
    }

    public void setExchangeRate(String sourceCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        BigDecimal sourceRate = getExchangeRate(sourceCurrencyCode);
        BigDecimal targetRate = getExchangeRate(targetCurrencyCode);
        BigDecimal crossRate = sourceRate.divide(targetRate, scale, roundMode);

        if (!inverse) {
            crossRate = targetRate.divide(sourceRate, scale, roundMode);
        }

        sourceRate = sourceRate.multiply(crossRate.divide(rate, scale, roundMode)).setScale(12, roundMode);
        rates.put(sourceCurrencyCode, sourceRate);
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
            BigDecimal newValue = getExchangeRate(newBaseCurrency, entry.getKey());
            if(inverse){
                newValue = BigDecimal.valueOf(1.0d).divide(newValue, scale, roundMode);
            }
            rates.put(entry.getKey(), newValue);
        }
        rates.put(baseCurrency, newBaseCurrencyRate);
        rates.put(newBaseCurrency, BigDecimal.valueOf(1.0d));
        baseCurrency = newBaseCurrency;
    }

    public void setInverse(boolean inverse) {
        if (inverse != this.inverse) {
            for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
                BigDecimal oldValue = entry.getValue();
                BigDecimal newValue = BigDecimal.valueOf(1.0d).divide(oldValue, scale, roundMode);
                rates.put(entry.getKey(), newValue);
            }
        }
    }
}
