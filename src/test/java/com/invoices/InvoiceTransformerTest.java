package com.invoices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceTransformerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testXmlTransformation() {
        ObjectNode invoice = mapper.createObjectNode();
        invoice.put("invoiceId", "INV-001");
        invoice.put("country", "DE");
        var lines = invoice.putArray("lines");
        lines.addObject().put("amount", 100).put("vatRate", 0.2);

        String xml = InvoiceTransformer.toXml(invoice);
        assertTrue(xml.contains("<Id>INV-001</Id>"));
        assertTrue(xml.contains("<Country>DE</Country>"));
        assertTrue(xml.contains("<Total>120.0</Total>"));
    }
}
