package com.invoices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceValidatorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testValidInvoiceDE() {
        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("country", "DE");
        invoice.put("customerId", "12345678A");
        var lines = invoice.putArray("lines");
        lines.addObject().put("amount", 100).put("vatRate", 0.19);

        assertTrue(InvoiceValidator.validate(invoice));
    }

    @Test
    void testInvalidCountry() {
        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("country", "IT");
        invoice.put("customerId", "12345678A");
        invoice.putArray("lines").addObject().put("amount", 100);

        assertFalse(InvoiceValidator.validate(invoice));
    }

    @Test
    void testNegativeAmount() {
        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("country", "ES");
        invoice.put("customerId", "12345678A");
        invoice.putArray("lines").addObject().put("amount", -50);

        assertFalse(InvoiceValidator.validate(invoice));
    }

    @Test
    void testInvalidCustomerIdFR() {
        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("country", "FR");
        invoice.put("customerId", "123456789"); // mal formato
        invoice.putArray("lines").addObject().put("amount", 100);

        assertFalse(InvoiceValidator.validate(invoice));
    }
}
