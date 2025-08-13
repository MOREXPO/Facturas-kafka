package com.invoices;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InvoiceValidator {

    private static final Set<String> ISO_COUNTRIES = new HashSet<>(Arrays.asList("DE", "ES", "FR"));

    public static boolean validate(JsonNode invoice) {
        if (invoice == null) return false;

        // Country validation
        String country = invoice.has("country") ? invoice.get("country").asText() : "";
        if (!ISO_COUNTRIES.contains(country)) return false;

        // CustomerId validation according to country
        String customerId = invoice.has("customerId") ? invoice.get("customerId").asText() : "";
        if (!validateCustomerId(customerId, country)) return false;

        // Line validation
        JsonNode lines = invoice.get("lines");
        if (lines == null || !lines.isArray() || lines.size() == 0) return false;
        for (JsonNode line : lines) {
            if (!line.has("amount") || line.get("amount").asDouble() <= 0) return false;
        }

        return true;
    }

    private static boolean validateCustomerId(String customerId, String country) {
        if (country.equals("DE")) {
            return customerId.matches("\\d{8}[A-Z]");
        } else if (country.equals("ES")) {
            return customerId.matches("\\d{8}[A-Z]");
        } else if (country.equals("FR")) {
            return customerId.matches("[A-Z]{2}\\d{9}");
        }
        return false;
    }
}
