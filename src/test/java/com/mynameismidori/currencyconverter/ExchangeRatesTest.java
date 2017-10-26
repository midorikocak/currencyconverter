package com.mynameismidori.currencyconverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ExchangeRatesTest {
    private Map<String, BigDecimal> rates;
    private Map<String, BigDecimal> ratesToCheck;
    private ExchangeRates exchangeRates;
    private long timestamp;
    private String baseCurrency;

    @Before
    public void setUp() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (Reader reader = new InputStreamReader(classLoader.getResourceAsStream("example-currencies.json"), "UTF-8")) {
            Gson gson = new GsonBuilder().create();


            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonObject ratesObject = jsonObject.get("rates").getAsJsonObject();
            timestamp = jsonObject.get("timestamp").getAsLong();

            Type typeOfHashMap = new TypeToken<HashMap<String, BigDecimal>>() {
            }.getType();
            rates = new Gson().fromJson(ratesObject, typeOfHashMap);
            ratesToCheck = new Gson().fromJson(ratesObject, typeOfHashMap);
            baseCurrency = "USD";
            exchangeRates = new ExchangeRates(timestamp, ratesToCheck, baseCurrency);
            Set<String> currentRates = exchangeRates.listAvailableCurrencies();
        }
    }

    @After
    public void tearDown(){
        setBaseCurrency("USD");
    }

    public void setBaseCurrency(String currencyCode) {
        baseCurrency = currencyCode;
        exchangeRates.setBaseCurrency(baseCurrency);
    }


    @Test
    public void testGetExchangeRateBase() {
        assertEquals(1.0d, exchangeRates.getExchangeRate(baseCurrency).doubleValue(), 0);
    }

    @Test
    public void testGetExchangeRateBaseBaseChanged() {
        setBaseCurrency("CZK");
        assertEquals(1.0d, exchangeRates.getExchangeRate(baseCurrency).doubleValue(), 0);
    }

    @Test
    public void testGetExchangeRateKoruna() {
        assertEquals(rates.get("CZK"), exchangeRates.getExchangeRate("CZK"));
    }
    @Test
    public void testGetExchangeRateKorunaBaseChanged() {
        setBaseCurrency("CZK");
        assertEquals(BigDecimal.valueOf(1.0d), exchangeRates.getExchangeRate("CZK"));
    }

    @Test
    public void testGetExchangeRate() {

        BigDecimal sourceRate = BigDecimal.valueOf(rates.get("EUR").doubleValue());
        BigDecimal targetRate = BigDecimal.valueOf(rates.get("CZK").doubleValue());

        BigDecimal crossRate = targetRate.divide(sourceRate, 12, BigDecimal.ROUND_HALF_UP);

        double expected = crossRate.doubleValue();
        double actual = exchangeRates.getExchangeRate("EUR", "CZK").doubleValue();
        assertEquals(expected, actual, 0.00001);
    }

    @Test
    public void testGetExchangeRateBaseChanged() {
        setBaseCurrency("CZK");
        BigDecimal sourceRate = BigDecimal.valueOf(rates.get("EUR").doubleValue());
        BigDecimal targetRate = BigDecimal.valueOf(rates.get("CZK").doubleValue());

        BigDecimal crossRate = targetRate.divide(sourceRate, 12, BigDecimal.ROUND_HALF_UP);

        double expected = crossRate.doubleValue();
        double actual = exchangeRates.getExchangeRate("EUR", "CZK").doubleValue();
        assertEquals(expected, actual, 0.00001);
    }



    @Test
    public void testGetExchangeRateReverse() {

        BigDecimal sourceRate = BigDecimal.valueOf(rates.get("CZK").doubleValue());
        BigDecimal targetRate = BigDecimal.valueOf(rates.get("EUR").doubleValue());

        BigDecimal crossRate = targetRate.divide(sourceRate, 12, BigDecimal.ROUND_HALF_UP);
        double expected = crossRate.doubleValue();
        double actual = exchangeRates.getExchangeRate("CZK", "EUR").doubleValue();
        assertEquals(expected, actual, 0);
    }

    @Test
    public void testGetExchangeRateReverseBaseChanged() {
        setBaseCurrency("CZK");
        BigDecimal sourceRate = BigDecimal.valueOf(rates.get("CZK").doubleValue());
        BigDecimal targetRate = BigDecimal.valueOf(rates.get("EUR").doubleValue());

        BigDecimal crossRate = targetRate.divide(sourceRate, 12, BigDecimal.ROUND_HALF_UP);
        double expected = crossRate.doubleValue();
        double actual = exchangeRates.getExchangeRate("CZK", "EUR").doubleValue();
        assertEquals(expected, actual, 0);
    }

    @Test
    public void testSetExchangeRate() {
        exchangeRates.setExchangeRate("EUR", "CZK", BigDecimal.valueOf(25.0));
        double rate = exchangeRates.getExchangeRate("EUR", "CZK").doubleValue();

        assertEquals(25, rate, 0);
    }

    @Test
    public void testSetExchangeRateBaseChanged() {
        setBaseCurrency("CZK");
        exchangeRates.setExchangeRate("EUR", "CZK", BigDecimal.valueOf(25.0));
        double rate = exchangeRates.getExchangeRate("EUR", "CZK").doubleValue();

        assertEquals(25, rate, 0);
    }

    @Test
    public void testChangeBaseCurrency() {
        exchangeRates.setExchangeRate("EUR", "CZK", BigDecimal.valueOf(25.0));
        setBaseCurrency("CZK");
        exchangeRates.setInverse(true);
        double rate = exchangeRates.getExchangeRate("EUR").doubleValue();
        assertEquals(25, rate, 0);
    }
}