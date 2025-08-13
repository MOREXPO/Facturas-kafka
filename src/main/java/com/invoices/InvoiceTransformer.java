package com.invoices;

import com.fasterxml.jackson.databind.JsonNode;

public class InvoiceTransformer {

    public static String toXml(JsonNode invoice) {
        String invoiceId = invoice.get("invoiceId").asText();
        String country = invoice.get("country").asText();
        JsonNode lines = invoice.get("lines");

        double total = 0;
        for (JsonNode line : lines) {
            double amount = line.get("amount").asDouble();
            double vat = line.get("vatRate").asDouble();
            total += amount * (1 + vat);
        }

        return "<Invoice>\n" +
                "  <Id>" + escapeXml(invoiceId) + "</Id>\n" +
                "  <Country>" + escapeXml(country) + "</Country>\n" +
                "  <Total>" + total + "</Total>\n" +
                "</Invoice>";
    }

    private static String escapeXml(String value) {
        if (value == null)
            return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
