package ru.otus.sqlmongomigrationtool.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.sqlmongomigrationtool.domain.Author;
import ru.otus.sqlmongomigrationtool.domain.Book;
import ru.otus.sqlmongomigrationtool.domain.Comment;
import ru.otus.sqlmongomigrationtool.domain.Genre;

@ChangeLog
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "dropDb", runAlways = true, author = "Diatessaron")
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthor", runAlways = true, author = "Diatessaron")
    public void insertAuthor(MongoTemplate template) {
        template.save(new Author("James Joyce"), "authors");
    }

    @ChangeSet(order = "003", id = "insertGenre", runAlways = true, author = "Diatessaron")
    public void insertGenre(MongoTemplate template) {
        template.save(new Genre("Modernist novel"), "genres");
    }

    @ChangeSet(order = "004", id = "insertBook", runAlways = true, author = "Diatessaron")
    public void insertBook(MongoTemplate template) {
        template.save(new Book("Ulysses", new Author("James Joyce"),
                new Genre("Modernist novel")), "books");
    }

    @ChangeSet(order = "005", id = "insertComment", runAlways = true, author = "Diatessaron")
    public void insertComment(MongoTemplate template) {
        template.save(new Comment("Published in 1922", "Ulysses"), "comments");
    }
}
