package com.mynameismidori.currencyconverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.*;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CurrencyConverterTest {
    private CurrencyConverter currencyConverter;
    private Map<String, Double> rates;

    @Before
    public void setUp() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (Reader reader = new InputStreamReader(classLoader.getResourceAsStream("example-currencies.json"), "UTF-8")) {
            Gson gson = new GsonBuilder().create();


            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonObject ratesObject = jsonObject.get("rates").getAsJsonObject();

            Type typeOfHashMap = new TypeToken<HashMap<String, Double>>() {
            }.getType();
            rates = new Gson().fromJson(ratesObject, typeOfHashMap);
        }
        currencyConverter = new CurrencyConverter();

    }

    @Test
    public void testConvertAmount() {

        System.out.println(rates.get("EUR"));
    }

}