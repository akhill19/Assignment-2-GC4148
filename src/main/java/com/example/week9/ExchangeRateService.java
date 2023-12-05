package com.example.demo1;

import javafx.application.Platform;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateService {
    private static final String API_KEY = "1cfc282643868635b21603a4"; // Replace with your actual API key

    public void fetchExchangeRates(String targetCurrency, DataListener listener) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.exchangerate-api.com/v4/latest/" + targetCurrency;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Map<String, Double> exchangeRates = parseExchangeRates(responseBody);

                    // Ensure UI update on JavaFX Application Thread
                    Platform.runLater(() -> {
                        listener.onDataFetched(exchangeRates);
                    });
                } else {
                    // Ensure UI update on JavaFX Application Thread
                    Platform.runLater(() -> {
                        listener.onError("Failed to fetch exchange rates: " + response.code() + " - " + response.message());
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Ensure UI update on JavaFX Application Thread
                Platform.runLater(() -> {
                    listener.onError("Failed to fetch exchange rates. Check your internet connection.");
                });
            }
        });
    }

    private Map<String, Double> parseExchangeRates(String responseBody) {
        Map<String, Double> exchangeRates = new HashMap<>();
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject rates = jsonObject.getJSONObject("rates");

        for (String currency : rates.keySet()) {
            exchangeRates.put(currency, rates.getDouble(currency));
        }

        return exchangeRates;
    }

    @FunctionalInterface
    public interface DataListener {
        void onDataFetched(Map<String, Double> exchangeRates);

        default void onError(String errorMessage) {
            // Default implementation for onError (can be overridden)
        }
    }
}
