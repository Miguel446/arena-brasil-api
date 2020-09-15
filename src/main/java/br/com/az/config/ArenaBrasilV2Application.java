package br.com.az.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SpringBootApplication
@EntityScan(basePackages = { "br.com.az.model" })
@ComponentScan(basePackages = { "br.com.*" })
@EnableJpaRepositories(basePackages = { "br.com.az.repository" })
public class ArenaBrasilV2Application {

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
		return converter;
	}

	public static void main(String[] args) {
		SpringApplication.run(ArenaBrasilV2Application.class, args);
	}

	@Bean
	public Formatter<LocalDate> localDateFormatter() {
		return new Formatter<LocalDate>() {
			@Override
			public LocalDate parse(String text, Locale locale) throws ParseException {
				return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
			}

			@Override
			public String print(LocalDate object, Locale locale) {
				return DateTimeFormatter.ISO_DATE.format(object);
			}
		};
	}

}
