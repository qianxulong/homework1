package com.pojo;

import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;

@Table(name = "book")
public class Book {
    @Id
    @Column(name = "id")
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    @Column(name = "title")
    private String title;


    @Column(name = "sub_title")
    private String sub_title;


    @Column(name="price")
    private Integer price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Book(Integer id, String title, String sub_title, Integer price) {
        this.id = id;
        this.title = title;
        this.sub_title = sub_title;
        this.price = price;
    }

    public Book() {
    }
}
