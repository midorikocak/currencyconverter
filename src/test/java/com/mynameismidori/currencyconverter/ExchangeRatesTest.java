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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ExchangeRatesTest {
    private Map<String, BigDecimal> rates;
    private ExchangeRates exchangeRates;
    private long timestamp;

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
            exchangeRates = new ExchangeRates(timestamp, rates, "USD");
            Set<String> currentRates = exchangeRates.listAvailableCurrencies();
            System.out.println(currentRates);
        }
    }


    @Test
    public void testGetExchangeRateBase() {
        assertEquals(1.0d, exchangeRates.getExchangeRate("USD").doubleValue(), 0);
    }

    @Test
    public void testGetExchangeRateKoruna() {
        assertEquals(rates.get("CZK"), exchangeRates.getExchangeRate("CZK"));
    }

    @Test
    public void testGetExchangeRate() {
        double crossRate = exchangeRates.getCrossRate(rates.get("TRY"), rates.get("CZK")).doubleValue();
        assertEquals(crossRate, exchangeRates.getExchangeRate("TRY", "CZK").doubleValue(), 0);
    }

    @Test
    public void testSetExchangeRate() {
        exchangeRates.setExchangeRate("EUR", "CZK", BigDecimal.valueOf(25.0));
        double rate = exchangeRates.getExchangeRate("EUR", "CZK").doubleValue();

        assertEquals(25, rate, 0);
    }

    @Test
    public void testChangeBaseCurrency() {
        exchangeRates.setExchangeRate("EUR", "CZK", BigDecimal.valueOf(25.0));
        exchangeRates.setBaseCurrency("CZK");
        double rate = exchangeRates.getExchangeRate("EUR").doubleValue();

        assertEquals(25, rate, 0);
    }


}