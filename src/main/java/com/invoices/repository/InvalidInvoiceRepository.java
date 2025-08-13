package com.invoices.repository;

import com.invoices.entity.InvalidInvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidInvoiceRepository extends JpaRepository<InvalidInvoiceEntity, Long> {
}
