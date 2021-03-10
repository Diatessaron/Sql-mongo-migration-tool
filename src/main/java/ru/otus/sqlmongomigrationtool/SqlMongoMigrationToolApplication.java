package ru.otus.sqlmongomigrationtool;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@EnableBatchProcessing
@SpringBootApplication
public class SqlMongoMigrationToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlMongoMigrationToolApplication.class, args);
    }
}
