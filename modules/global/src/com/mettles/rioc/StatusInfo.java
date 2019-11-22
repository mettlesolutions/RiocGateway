package com.mettles.rioc;

import java.io.Serializable;

public class StatusInfo implements Serializable {

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

    protected String esmdTransactionId;
    protected String transType;
    protected String uniqueID;
    protected String result;
    protected String status;
    protected String statusCode;
}
