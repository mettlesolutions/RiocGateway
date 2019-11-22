package com.mettles.rioc.portal.jsonobjects;

public class X12278ErrorJsonObject {

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getError_context() {
        return error_context;
    }

    public void setError_context(String error_context) {
        this.error_context = error_context;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    private String error_code;
    private String severity;
    private String error_context;
    private String error_description;
    private String esmd_transaction_id;
    private String trans_type;
}
