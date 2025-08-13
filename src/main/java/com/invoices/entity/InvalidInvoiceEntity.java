package com.invoices.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invalid_invoices")
public class InvalidInvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceId;
    private String customerId;
    private String country;
    private LocalDate issueDate;

    @Lob
    private String rawJson; // Guarda el JSON original por simplicidad

    public InvalidInvoiceEntity() {
    }

    public InvalidInvoiceEntity(String invoiceId, String customerId, String country, LocalDate issueDate,
            String rawJson) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.country = country;
        this.issueDate = issueDate;
        this.rawJson = rawJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

}
