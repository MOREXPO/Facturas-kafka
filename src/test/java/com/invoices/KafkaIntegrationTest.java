package com.invoices;

import com.invoices.entity.InvalidInvoiceEntity;
import com.invoices.repository.InvalidInvoiceRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"incoming-invoices", "validated-invoices"})
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private InvalidInvoiceRepository invalidRepo;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void testInvoiceProcessingFlow() throws Exception {
        // JSON válido
        String validInvoice = """
                {"invoiceId":"INV-001","customerId":"12345678A","country":"DE","issueDate":"2025-08-13","lines":[{"amount":100,"vatRate":0.2}]}
                """;

        // JSON inválido (customerId incorrecto)
        String invalidInvoice = """
                {"invoiceId":"INV-002","customerId":"XXXX","country":"DE","issueDate":"2025-08-13","lines":[{"amount":100,"vatRate":0.2}]}
                """;

        // Publica mensajes en incoming-invoices
        kafkaTemplate.send("incoming-invoices", validInvoice);
        kafkaTemplate.send("incoming-invoices", invalidInvoice);
        kafkaTemplate.flush();

        // Espera a que Kafka Streams procese
        Thread.sleep(2000);

        // Configura consumidor para validated-invoices
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
        var consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "validated-invoices");

        // Verifica que la factura válida se transformó a XML
        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "validated-invoices");
        assertThat(record.value()).contains("<Id>INV-001</Id>");
        assertThat(record.value()).contains("<Total>120.0</Total>");

        consumer.close();

        // Verifica que la factura inválida se guardó en H2
        var invalids = invalidRepo.findAll();
        assertThat(invalids).hasSize(1);
        InvalidInvoiceEntity savedInvalid = invalids.get(0);
        assertThat(savedInvalid.getInvoiceId()).isEqualTo("INV-002");
        assertThat(savedInvalid.getCustomerId()).isEqualTo("XXXX");
    }
}
