package ru.otus.sqlmongomigrationtool.domain.jpa;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genres")
public class JpaGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;

    public JpaGenre() {
    }

    public JpaGenre(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaGenre genre = (JpaGenre) o;
        return name.equals(genre.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
