package ru.otus.sqlmongomigrationtool.services;

import org.springframework.stereotype.Service;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaAuthor;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaBook;
import ru.otus.sqlmongomigrationtool.domain.jpa.JpaGenre;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Service
public class BookServiceImpl implements BookService {
    private final EntityManager entityManager;

    public BookServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public JpaBook processBook(MongoBook book) {
        return new JpaBook(book.getTitle(), getAuthor(book.getAuthor().getName()),
                getGenre(book.getGenre().getName()));
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
