package com.mettles.rioc;

import java.io.Serializable;
import java.util.Hashtable;

public class X12278ValidationStatus implements Serializable {

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEDI() {
        return EDI;
    }

    public void setEDI(String EDI) {
        this.EDI = EDI;
    }

    private int statusCode;
    private String status;
    private String EDI;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPwk() {
        return pwk;
    }

    public void setPwk(String pwk) {
        this.pwk = pwk;
    }

    public Hashtable<String, String> getError999Key() {
        return error999Key;
    }

    public void setError999Key(Hashtable<String, String> error999Key) {
        this.error999Key = error999Key;
    }

    private String errorMessage;
    private String pwk;
    private Hashtable<String, String> error999Key = new Hashtable<String, String>();



}
