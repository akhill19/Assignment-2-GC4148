package com.example.demo1;

import java.util.Map;

@FunctionalInterface
public interface DataListener {
    void onDataFetched(Map<String, Double> exchangeRates);

    default void onError(String errorMessage) {

    }
}

