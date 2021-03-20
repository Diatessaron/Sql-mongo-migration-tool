package ru.otus.sqlmongomigrationtool.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "authors")
public class MongoAuthor {
    @Id
    private String id;
    private String name;

    public MongoAuthor() {
    }

    public MongoAuthor(String name) {
        this.name = name;
    }

    public MongoAuthor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoAuthor author = (MongoAuthor) o;
        return name.equals(author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
