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
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoAuthor;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoGenre;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;

@Configuration
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManager entityManager;

    private static final int CHUNK_SIZE = 5;

    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                     EntityManager entityManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManager = entityManager;
    }

    @StepScope
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

    @StepScope
    @Bean
    public ItemProcessor<MongoBook, JpaBook> bookProcessor() {
        return book -> new JpaBook(book.getTitle(), getAuthor(book.getAuthor().getName()),
                getGenre(book.getGenre().getName()));
    }

    @StepScope
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

    @StepScope
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

    @StepScope
    @Bean
    public ItemProcessor<MongoAuthor, JpaAuthor> authorProcessor() {
        return author -> new JpaAuthor(author.getName());
    }

    @StepScope
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

    @StepScope
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

    @StepScope
    @Bean
    public ItemProcessor<MongoGenre, JpaGenre> genreProcessor() {
        return genre -> new JpaGenre(genre.getName());
    }

    @StepScope
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

    private JpaAuthor getAuthor(String name) {
        final TypedQuery<JpaAuthor> query = entityManager.createQuery
                ("select a from JpaAuthor a where a.name = :name", JpaAuthor.class);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

    private JpaGenre getGenre(String name) {
        final TypedQuery<JpaGenre> query = entityManager.createQuery
                ("select g from JpaGenre g where g.name = :name", JpaGenre.class);
        query.setParameter("name", name);

        return query.getSingleResult();
    }
}
