package com.invoices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoices.entity.InvalidInvoiceEntity;
import com.invoices.repository.InvalidInvoiceRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class InvoiceProcessor {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final InvalidInvoiceRepository invalidRepo;

    public InvoiceProcessor(InvalidInvoiceRepository invalidRepo) {
        this.invalidRepo = invalidRepo;
    }

    public List<String> process(String jsonString) {
        try {
            JsonNode invoice = objectMapper.readTree(jsonString);
            if (!InvoiceValidator.validate(invoice)) {
                InvalidInvoiceEntity invalid = new InvalidInvoiceEntity(
                        invoice.has("invoiceId") ? invoice.get("invoiceId").asText() : null,
                        invoice.has("customerId") ? invoice.get("customerId").asText() : null,
                        invoice.has("country") ? invoice.get("country").asText() : null,
                        invoice.has("issueDate") ? LocalDate.parse(invoice.get("issueDate").asText()) : null,
                        jsonString
                );
                invalidRepo.save(invalid);
                return Collections.emptyList();
            }
            return Collections.singletonList(InvoiceTransformer.toXml(invoice));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
