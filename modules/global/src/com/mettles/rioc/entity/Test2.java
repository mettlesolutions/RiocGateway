package com.mettles.rioc.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "RIOC_TEST2")
@Entity(name = "rioc_Test2")
public class Test2 extends StandardEntity {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Column(name = "title")
    protected String title;

    @Column(name = "comments", length = 512)
    protected String comments;

}