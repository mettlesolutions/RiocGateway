package com.mettles.rioc.portal.jsonobjects;

public class StatusChangesJsonObject {
    private String esmd_transaction_id;
    private String split_number;

    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
    }

    public String getSplit_number() {
        return split_number;
    }

    public void setSplit_number(String split_number) {
        this.split_number = split_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String status;
    private String time;
    private String title;


}
