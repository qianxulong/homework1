package com.controller;

import com.pojo.Book;
import com.service.BookService;
import com.service.ElasticService;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class BookController {
    @Autowired
    BookService bookService;

    @Autowired
    ElasticService elasticService;

    @RequestMapping("/book")
    @ResponseBody
    public Book getBookById(){
        return bookService.getBookById(2);
    }

    @RequestMapping("/create_index")
    @ResponseBody
    public boolean createIndex() throws Exception{
        boolean index = elasticService.createIndex();
        return index;
    }

    @RequestMapping("/book_update")
    @ResponseBody
    public boolean update() throws Exception{
        Book book = new Book(2,"格林童话","雪中悍刀行第一章节",60);
        int update = bookService.update(book);
        boolean b = elasticService.saveOrUpdate(book);
        return b;

    }
    @RequestMapping("/book_create")
    @ResponseBody
    public boolean create() throws Exception{
        Book book = new Book();
        book.setId(5);
        book.setPrice(60);
        book.setTitle("安徒生童话");
        book.setSub_title("安徒生童话第三章节");
        int num = bookService.save(book);
        boolean b = elasticService.saveOrUpdate(book);
        return b;
    }

    @RequestMapping("/book_delete")
    @ResponseBody
    public boolean delete() throws Exception{
        int num = bookService.deleteById(4);
        boolean b = elasticService.deleteById(4);
        return b;
    }

    @RequestMapping("/query_all")
    public String queryAll(HttpServletRequest request) throws Exception{
        List<Book> bookList = elasticService.queryAll();
        request.setAttribute("books",bookList);
        return "book";
    }

    @RequestMapping("/search")
    public String blur_search(HttpServletRequest request) throws Exception{
        String search_word = request.getParameter("search_word");
        List<Book> bookList = elasticService.queryMultiMatch(search_word);
        request.setAttribute("books",bookList);
        return "book";
    }

}
