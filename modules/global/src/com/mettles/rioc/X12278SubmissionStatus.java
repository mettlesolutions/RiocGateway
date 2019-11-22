package com.mettles.rioc;


import com.mettles.rioc.ErrorInfo;
import com.mettles.rioc.entity.TransType;

import java.io.Serializable;
import java.util.ArrayList;

public class X12278SubmissionStatus implements Serializable {
    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getXdrstatusmessage() {
        return xdrstatusmessage;
    }

    public void setXdrstatusmessage(String xdrstatusmessage) {
        this.xdrstatusmessage = xdrstatusmessage;
    }

    public ArrayList<ErrorInfo> getSegErrs() {
        return segErrs;
    }

    public void setSegErrs(ArrayList<ErrorInfo> segErrs) {
        this.segErrs = segErrs;
    }

    public ArrayList<StatusInfo> getSegStatus() {
        return segStatus;
    }

    public void setSegStatus(ArrayList<StatusInfo> segStatus) {
        this.segStatus = segStatus;
    }


    public String getEsmdTransactionId() {
        return esmdTransactionId;
    }

    public void setEsmdTransactionId(String esmdTransactionId) {
        this.esmdTransactionId = esmdTransactionId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }


    private String uniqueID;
    private String errorCode;
    private String errorMessage;
    protected String transType;
    protected String esmdTransactionId;
    private String xdrstatusmessage;
    private ArrayList<ErrorInfo> segErrs;
    private ArrayList<StatusInfo> segStatus;

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    */
    /*
    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    */


    //private String status;

    /*
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    */
    //private String statusCode;
    //private String errorDescription;

    /*
    public String getErrorCtx() {
        return errorCtx;
    }

    public void setErrorCtx(String errorCtx) {
        this.errorCtx = errorCtx;
    }

    private String errorCtx;
    */




    /*public TransType getTransType() {
            return transType == null ? null : TransType.fromId(transType);
        }

        public void setTransType(TransType transType) {
            this.transType = transType == null ? null : transType.getId();
        }

        protected String transType = TransType.X12.getId();
        */
   /*

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    protected String result;

    public String getXdrstatus() {
        return xdrstatus;
    }

    public void setXdrstatus(String xdrstatus) {
        this.xdrstatus = xdrstatus;
    }



    private String  xdrstatus;


    public String getDocUniqueID() {
        return docUniqueID;
    }

    public void setDocUniqueID(String docUniqueID) {
        this.docUniqueID = docUniqueID;
    }

    public String getDocSetAvail() {
        return docSetAvail;
    }

    public void setDocSetAvail(String docSetAvail) {
        this.docSetAvail = docSetAvail;
    }

    private String docUniqueID;
    private String docSetAvail;

    */

}
