package ru.otus.sqlmongomigrationtool.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoAuthor;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoGenre;
import ru.otus.sqlmongomigrationtool.services.BookService;

import javax.persistence.EntityManager;
import java.util.HashMap;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManager entityManager;
    private final BookService bookService;

    private static final int CHUNK_SIZE = 5;

    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                     EntityManager entityManager, BookService bookService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManager = entityManager;
        this.bookService = bookService;
    }

    @Bean
    public MongoItemReader<MongoBook> bookReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<MongoBook>()
                .name("mongoBookItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoBook.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<MongoBook, JpaBook> bookProcessor() {
        return bookService::processBook;
    }

    @Bean
    public JpaItemWriter<JpaBook> bookWriter() {
        return new JpaItemWriterBuilder<JpaBook>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .build();
    }

    @Bean
    public Step bookMigrationStep(JpaItemWriter<JpaBook> bookWriter,
                                  ItemReader<MongoBook> bookReader,
                                  ItemProcessor<MongoBook, JpaBook> bookProcessor) {
        return stepBuilderFactory.get("bookMigrationStep")
                .<MongoBook, JpaBook>chunk(CHUNK_SIZE)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public MongoItemReader<MongoAuthor> authorReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<MongoAuthor>()
                .name("mongoAuthorItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoAuthor.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<MongoAuthor, JpaAuthor> authorProcessor() {
        return author -> new JpaAuthor(author.getName());
    }

    @Bean
    public JpaItemWriter<JpaAuthor> authorWriter() {
        return new JpaItemWriterBuilder<JpaAuthor>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .build();
    }

    @Bean
    public Step authorMigrationStep(JpaItemWriter<JpaAuthor> authorWriter,
                                    ItemReader<MongoAuthor> authorReader,
                                    ItemProcessor<MongoAuthor, JpaAuthor> authorProcessor) {
        return stepBuilderFactory.get("authorMigrationStep")
                .<MongoAuthor, JpaAuthor>chunk(CHUNK_SIZE)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    @Bean
    public MongoItemReader<MongoGenre> genreReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<MongoGenre>()
                .name("mongoGenreItemReader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(MongoGenre.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor<MongoGenre, JpaGenre> genreProcessor() {
        return genre -> new JpaGenre(genre.getName());
    }

    @Bean
    public JpaItemWriter<JpaGenre> genreWriter() {
        return new JpaItemWriterBuilder<JpaGenre>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .build();
    }

    @Bean
    public Step genreMigrationStep(JpaItemWriter<JpaGenre> genreWriter,
                                   ItemReader<MongoGenre> genreReader,
                                   ItemProcessor<MongoGenre, JpaGenre> genreProcessor) {
        return stepBuilderFactory.get("genreMigrationStep")
                .<MongoGenre, JpaGenre>chunk(CHUNK_SIZE)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Job importUserJob(Step bookMigrationStep, Step authorMigrationStep, Step genreMigrationStep) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .start(authorMigrationStep)
                .next(genreMigrationStep)
                .next(bookMigrationStep)
                .build();
    }
}
