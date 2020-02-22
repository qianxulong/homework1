package com.service.impl;

import com.mapper.BookMapper;
import com.pojo.Book;
import com.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bookService")
public class BookServiceImpl implements BookService {
    @Autowired
    BookMapper bookMapper;

    @Override
    public Book getBookById(int id) {
        return bookMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Book> getAll() {
        return bookMapper.selectAll();
    }

    @Override
    public int deleteById(int id) {
        return bookMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int update(Book book) {
        return bookMapper.updateByPrimaryKey(book);
    }

    @Override
    public int save(Book book) {
        return bookMapper.insert(book);
    }
}
