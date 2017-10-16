package com.mynameismidori.currencyconverter;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CurrencyConverterTest {
    private CurrencyConverter currencyConverter;
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
            exchangeRates.setBaseCurrency("CZK");
        }
        currencyConverter = new CurrencyConverter(exchangeRates, "EUR", "CZK");

    }

    @Test
    public void testConvertAmountToBaseCurrency() {
        BigDecimal amount = BigDecimal.valueOf(15.0d);
        double expected = amount.multiply(rates.get("EUR")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double result = currencyConverter.convert(amount.doubleValue());
        assertEquals(expected, result, 0);
    }

    @Test
    public void testConvertAmountFromBaseCurrency() {
        BigDecimal amount = BigDecimal.valueOf(300.0d);
        double expected = amount.multiply(exchangeRates.getExchangeRate("CZK", "EUR")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double result = currencyConverter.convert(amount.doubleValue(), "CZK", "EUR");
        assertEquals(expected, result, 0);
    }

    @Test
    public void testConvertAmountNotBaseCurrency() {

        BigDecimal amount = BigDecimal.valueOf(100);
        double expected = amount.multiply(exchangeRates.getExchangeRate("EUR", "TRY")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double result = currencyConverter.convert(amount.doubleValue(), "EUR", "TRY");
        assertEquals(expected, result, 0);
    }

    @Test
    public void testSetCustomTemporaryCurrencyRate() {
        BigDecimal amount = BigDecimal.valueOf(100);
        double expected = amount.multiply(BigDecimal.valueOf(4.5d)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double result = currencyConverter.convert(amount.doubleValue(), "EUR", "TRY", 4.5d);
        assertEquals(expected, result, 0);
    }


}