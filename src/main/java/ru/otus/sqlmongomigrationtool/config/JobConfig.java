package ru.otus.sqlmongomigrationtool.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaAuthor;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaBook;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaGenre;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int CHUNK_SIZE = 5;

    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                     EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @StepScope
    @Bean
    public MongoItemReader<MongoBook> reader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<MongoBook>()
                .name("mongoItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoBook.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<MongoBook, JpaBook> processor() {
        return book -> new JpaBook(book.getTitle(), new JpaAuthor(book.getAuthor().getName()),
                new JpaGenre(book.getGenre().getName()));
    }

    @StepScope
    @Bean
    public JpaItemWriter<JpaBook> writer() {
        return new JpaItemWriterBuilder<JpaBook>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job importUserJob(Step libraryMigrationStep) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(libraryMigrationStep)
                .end()
                .build();
    }

    @Bean
    public Step libraryMigrationStep(JpaItemWriter<JpaBook> writer,
                                     ItemReader<MongoBook> reader,
                                     ItemProcessor<MongoBook, JpaBook> itemProcessor) {
        return stepBuilderFactory.get("libraryMigrationStep")
                .<MongoBook, JpaBook>chunk(CHUNK_SIZE)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
