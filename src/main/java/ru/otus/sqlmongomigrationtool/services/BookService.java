package ru.otus.sqlmongomigrationtool.services;

import ru.otus.sqlmongomigrationtool.domain.jpa.JpaBook;
import ru.otus.sqlmongomigrationtool.domain.mongo.MongoBook;

public interface BookService {
    JpaBook processBook(MongoBook book);
}
