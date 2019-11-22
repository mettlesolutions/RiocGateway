package com.mettles.rioc;

import java.io.Serializable;

public class ErrorInfo implements Serializable {
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCtx() {
        return errorCtx;
    }

    public void setErrorCtx(String errorCtx) {
        this.errorCtx = errorCtx;
    }

    public String getErrorDesp() {
        return errorDesp;
    }

    public void setErrorDesp(String errorDesp) {
        this.errorDesp = errorDesp;
    }

    private String errorCtx;
    private String errorDesp;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    private String severity;

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    private String transType;

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    protected String uniqueID;

}
