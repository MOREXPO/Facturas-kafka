package com.invoices.controller;

import com.invoices.InvoiceProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceProcessor invoiceProcessor;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public InvoiceController(InvoiceProcessor invoiceProcessor, KafkaTemplate<String, String> kafkaTemplate) {
        this.invoiceProcessor = invoiceProcessor;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    @Operation(
            summary = "Process an electronic invoice",
            description = "Validates the invoice and transforms it to XML if valid. Invalid invoices are saved to the database.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Invoice JSON object to be processed",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                            {
                              "invoiceId": "INV-20240101-001",
                              "customerId": "12345678A",
                              "country": "DE",
                              "issueDate": "2024-01-01",
                              "lines": [
                                { "description": "Laptop", "amount": 200, "vatRate": 0.19 }
                              ]
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Invoice processed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": "OK",
                                      "xml": "<Invoice><Id>INV-20240101-001</Id>...</Invoice>"
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid invoice",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": "ERROR",
                                      "errorMessage": "Invoice is invalid"
                                    }
                                    """)
                            )
                    )
            }
    )
    public ResponseEntity<Map<String, Object>> processInvoice(@RequestBody String invoiceJson) {
        List<String> result = invoiceProcessor.process(invoiceJson);

        if (result.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "errorMessage", "Invoice is invalid"
                    )
            );
        }

        kafkaTemplate.send("incoming-invoices", invoiceJson);

        return ResponseEntity.ok(
                Map.of(
                        "status", "OK",
                        "xml", String.join("\n", result)
                )
        );
    }
}
