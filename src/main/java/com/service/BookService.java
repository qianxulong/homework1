package com.service;

import com.pojo.Book;

import java.util.List;

public interface BookService {
    public Book getBookById(int id);
    public List<Book> getAll();
    public int deleteById(int id);
    public int update(Book book);
    public int save(Book book);

}
