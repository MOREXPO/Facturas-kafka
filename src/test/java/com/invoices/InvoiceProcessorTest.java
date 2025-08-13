package com.invoices;

import com.invoices.entity.InvalidInvoiceEntity;
import com.invoices.repository.InvalidInvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InvoiceProcessorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testProcessValidInvoice() {
        InvalidInvoiceRepository repo = mock(InvalidInvoiceRepository.class);
        InvoiceProcessor processor = new InvoiceProcessor(repo);

        String json = """
                {"invoiceId":"INV-001","customerId":"12345678A","country":"DE","issueDate":"2025-08-13","lines":[{"amount":100,"vatRate":0.2}]}
                """;

        List<String> result = processor.process(json);
        assertEquals(1, result.size());
        verify(repo, never()).save(any(InvalidInvoiceEntity.class));
    }

    @Test
    void testProcessInvalidInvoice() {
        InvalidInvoiceRepository repo = mock(InvalidInvoiceRepository.class);
        InvoiceProcessor processor = new InvoiceProcessor(repo);

        String json = """
                {"invoiceId":"INV-001","customerId":"XXXX","country":"DE","issueDate":"2025-08-13","lines":[{"amount":100,"vatRate":0.2}]}
                """;

        List<String> result = processor.process(json);
        assertEquals(0, result.size());
        verify(repo, times(1)).save(any(InvalidInvoiceEntity.class));
    }
}
