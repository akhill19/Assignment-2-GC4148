package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class MainController {
    private ExchangeRateService exchangeRateService = new ExchangeRateService();
    private Map<String, String> countryToCurrencyMap;

    @FXML
    private TextField amountField;

    @FXML
    private ChoiceBox<String> countryChoiceBox;

    @FXML
    private Label resultLabel;

    @FXML
    private void initialize() {
        populateCountryChoiceBox();

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateResult();
        });

        countryChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateResult();
        });
    }




    private void populateCountryChoiceBox() {
        // Populate the choice box with country options and map them to currency codes
        countryToCurrencyMap = new HashMap<>();
        countryToCurrencyMap.put("United States", "USD");
        countryToCurrencyMap.put("European Union", "EUR");
        countryToCurrencyMap.put("United Kingdom", "GBP");

        countryChoiceBox.getItems().addAll(countryToCurrencyMap.keySet());
        countryChoiceBox.setValue("United States"); // Set a default value
    }

    private void updateResult() {
        try {
            String amountText = amountField.getText();
            String selectedCountry = countryChoiceBox.getValue();

            if (!amountText.isEmpty() && selectedCountry != null) {
                double amount = Double.parseDouble(amountText);

                if (countryToCurrencyMap.containsKey(selectedCountry)) {
                    String targetCurrency = countryToCurrencyMap.get(selectedCountry);

                    // Always convert from USD to the selected currency
                    String baseCurrency = "USD";

                    exchangeRateService.fetchExchangeRates(baseCurrency, exchangeRates -> {
                        if (exchangeRates != null && exchangeRates.containsKey(targetCurrency)) {
                            double exchangeRate = exchangeRates.get(targetCurrency);
                            double result = amount * exchangeRate;

                            Platform.runLater(() -> {
                                resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, targetCurrency, result, targetCurrency));
                            });
                        } else {
                            Platform.runLater(() -> {
                                resultLabel.setText("Failed to fetch exchange rates for " + targetCurrency);
                            });
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        resultLabel.setText("Currency data not found for selected country.");
                    });
                }
            }
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                resultLabel.setText("Please enter a valid amount.");
            });
        }
    }

}
