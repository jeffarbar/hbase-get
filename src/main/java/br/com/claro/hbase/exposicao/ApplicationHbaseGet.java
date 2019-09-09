package br.com.claro.hbase.exposicao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableAutoConfiguration
@SpringBootApplication
public class ApplicationHbaseGet {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationHbaseGet.class, args);
	}
}
