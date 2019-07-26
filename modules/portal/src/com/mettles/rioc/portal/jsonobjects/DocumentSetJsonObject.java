package com.mettles.rioc.portal.jsonobjects;

public class DocumentSetJsonObject {

    private String name;
    private Integer split_no;
    private String filename;
    private String document_type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSplit_no() {
        return split_no;
    }

    public void setSplit_no(Integer split_no) {
        this.split_no = split_no;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }



}
