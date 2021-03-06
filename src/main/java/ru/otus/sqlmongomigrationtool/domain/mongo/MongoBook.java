package ru.otus.sqlmongomigrationtool.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "books")
public class MongoBook {
    @Id
    private String id;
    private String title;
    private MongoAuthor author;
    private MongoGenre genre;

    public MongoBook() {
    }

    public MongoBook(String title, MongoAuthor author, MongoGenre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public MongoAuthor getAuthor() {
        return author;
    }

    public MongoGenre getGenre() {
        return genre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(MongoAuthor author) {
        this.author = author;
    }

    public void setGenre(MongoGenre genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoBook book = (MongoBook) o;
        return title.equals(book.title) && Objects.equals(author, book.author) &&
                Objects.equals(genre, book.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, genre);
    }

    @Override
    public String toString() {
        return "Title: " + title + '\n' +
                "Author: " + author.getName() + '\n' +
                "Genre: " + genre;
    }
}
