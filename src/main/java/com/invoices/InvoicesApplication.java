package com.invoices;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class InvoicesApplication {

	private final InvoiceProcessor invoiceProcessor;

	public InvoicesApplication(InvoiceProcessor invoiceProcessor) {
		this.invoiceProcessor = invoiceProcessor;
	}

	public static void main(String[] args) {
		SpringApplication.run(InvoicesApplication.class, args);
	}

	@Bean
	public KStream<String, String> invoicesStream(StreamsBuilder builder) {
		KStream<String, String> source = builder.stream(
				"incoming-invoices",
				Consumed.with(Serdes.String(), Serdes.String()));

		source.flatMapValues(invoiceProcessor::process)
				.to("validated-invoices", Produced.with(Serdes.String(), Serdes.String()));

		return source;
	}
}
