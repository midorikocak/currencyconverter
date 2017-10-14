package com.mynameismidori.currencyconverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class ExchangeRates {

    private long timestamp;
    private Map<String, Double> rates;
    private String baseCurrency;

    private final static int ROUND_MODE = BigDecimal.ROUND_HALF_DOWN;
    private final static int SCALE = 2;

    public ExchangeRates(long timestamp, Map<String, Double> rates, String baseCurrency) {
        this.timestamp = timestamp;
        this.rates = rates;
        this.baseCurrency = baseCurrency;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public double getExchangeRate(String sourceCurrencyCode, String targetCurrencyCode) {

        if (targetCurrencyCode.equals(baseCurrency)) {
            return getExchangeRate(sourceCurrencyCode);
        }

        double sourceRate = getExchangeRate(sourceCurrencyCode);
        double targetRate = getExchangeRate(targetCurrencyCode);

        BigDecimal crossRate = getCrossRate(sourceRate, targetRate);
        return crossRate.doubleValue();
    }

    public double getExchangeRate(String sourceCurrencyCode) {
        return rates.get(sourceCurrencyCode);
    }

    /*
    public void setExchangeRate(String sourceCurrencyCode, String targetCurrencyCode, double rate) {

        if (!targetCurrencyCode.equals(baseCurrency)) {
            double sourceRate = getExchangeRate(sourceCurrencyCode);
            double targetRate = getExchangeRate(targetCurrencyCode);

            BigDecimal crossRate = getCrossRate(sourceRate, targetRate);
            crossRate = crossRate.multiply(BigDecimal.valueOf(rate));
            rates.put(sourceCurrencyCode, crossRate.doubleValue());
        }
        else{
            setExchangeRate(sourceCurrencyCode, rate);
        }
    }
    */

    public void setExchangeRate(String sourceCurrencyCode, double rate) {
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

    void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    private BigDecimal getCrossRate(double sourceRate, double targetRate) {
        BigDecimal sourceRateValue = BigDecimal.valueOf(sourceRate);
        BigDecimal targetRateValue = BigDecimal.valueOf(targetRate);

        return sourceRateValue.divide(targetRateValue, SCALE, ROUND_MODE);
    }
}
