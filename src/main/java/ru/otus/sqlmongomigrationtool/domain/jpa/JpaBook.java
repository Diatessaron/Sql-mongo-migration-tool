package ru.otus.sqlmongomigrationtool.domain.jpa;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "books")
public class JpaBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title")
    private String title;
    @ManyToOne(targetEntity = JpaAuthor.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private JpaAuthor author;
    @ManyToOne(targetEntity = JpaGenre.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "genre_id")
    private JpaGenre genre;

    public JpaBook() {
    }

    public JpaBook(String title, JpaAuthor author, JpaGenre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public JpaBook(long id, String title, JpaAuthor author, JpaGenre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public JpaAuthor getAuthor() {
        return author;
    }

    public JpaGenre getGenre() {
        return genre;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(JpaAuthor author) {
        this.author = author;
    }

    public void setGenre(JpaGenre genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaBook book = (JpaBook) o;
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
