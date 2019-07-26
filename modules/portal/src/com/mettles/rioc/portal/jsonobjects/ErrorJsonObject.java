package com.mettles.rioc.portal.jsonobjects;

public class ErrorJsonObject {
    private String error_code;
    private String error_context;
    private String esmd_transaction_id;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_context() {
        return error_context;
    }

    public void setError_context(String error_context) {
        this.error_context = error_context;
    }

    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }



    private String name;
    private String severity;

    public Integer getSplit_number() {
        return split_number;
    }

    public void setSplit_number(Integer split_number) {
        this.split_number = split_number;
    }

    private Integer split_number;
}
