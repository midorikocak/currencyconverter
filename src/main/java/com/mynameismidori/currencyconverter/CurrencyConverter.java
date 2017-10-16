package com.mynameismidori.currencyconverter;


import java.math.BigDecimal;

public class CurrencyConverter {

    private String currentSourceCurrencyCode = "EUR";
    private String currentTargetCurrencyCode = "CZK";

    private ExchangeRates exchangeRates;

    public int roundMode = BigDecimal.ROUND_HALF_DOWN;
    public int scale = 2;

    public CurrencyConverter(ExchangeRates exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public CurrencyConverter(ExchangeRates exchangeRates, String sourceCurrencyCode, String targetCurrencyCode) {
        this.currentSourceCurrencyCode = sourceCurrencyCode;
        this.currentTargetCurrencyCode = targetCurrencyCode;
        this.exchangeRates = exchangeRates;
    }

    public Double convert(Double amount) {
        double result = amount;
        return convert(result, currentSourceCurrencyCode, currentTargetCurrencyCode);
    }

    public Double convert(Double amount, String sourceCurrencyCode) {
        double result = amount;
        return convert(result, sourceCurrencyCode, currentSourceCurrencyCode);
    }

    public Double convert(Double amount, String sourceCurrencyCode, String targetCurrencyCode) {

        BigDecimal rate = exchangeRates.getExchangeRate(sourceCurrencyCode, targetCurrencyCode);
        return BigDecimal.valueOf(amount).multiply(rate).setScale(scale, roundMode).doubleValue();
    }

    public Double convert(Double amount, String sourceCurrencyCode, String targetCurrencyCode, Double temporaryRate) {

        BigDecimal rate = exchangeRates.getExchangeRate(sourceCurrencyCode, targetCurrencyCode);
        return BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(temporaryRate)).setScale(scale, roundMode).doubleValue();
    }

}
