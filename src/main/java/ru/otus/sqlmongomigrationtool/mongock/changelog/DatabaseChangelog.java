package ru.otus.sqlmongomigrationtool.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoAuthor;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoGenre;

@ChangeLog
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "dropDb", runAlways = true, author = "Diatessaron")
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthor", runAlways = true, author = "Diatessaron")
    public void insertAuthor(MongockTemplate template) {
        template.save(new MongoAuthor("James Joyce"), "authors");
        template.save(new MongoAuthor("Pushkin"), "authors");
        template.save(new MongoAuthor("Lermontov"), "authors");
    }

    @ChangeSet(order = "003", id = "insertGenre", runAlways = true, author = "Diatessaron")
    public void insertGenre(MongockTemplate template) {
        template.save(new MongoGenre("Modernist Novel"), "genres");
        template.save(new MongoGenre("Classical Novel"), "genres");
        template.save(new MongoGenre("Classical Poem"), "genres");
    }

    @ChangeSet(order = "004", id = "insertBook", runAlways = true, author = "Diatessaron")
    public void insertBook(MongockTemplate template) {
        template.save(new MongoBook("Ulysses", new MongoAuthor("James Joyce"),
                new MongoGenre("Modernist Novel")), "books");
        template.save(new MongoBook("The Captain's Daughter", new MongoAuthor("Pushking"),
                new MongoGenre("Classical Novel")), "books");
        template.save(new MongoBook("Demon", new MongoAuthor("Lermontov"),
                new MongoGenre("Classical Poem")), "books");
    }
}
