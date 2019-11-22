package com.mettles.rioc;

import java.io.Serializable;

public class X12278SubmissionServiceStatus implements Serializable {

    public String getPaMessage() {
        return paMessage;
    }

    public void setPaMessage(String paMessage) {
        this.paMessage = paMessage;
    }

    public String getPaUTN() {
        return paUTN;
    }

    public void setPaUTN(String paUTN) {
        this.paUTN = paUTN;
    }

    /*public String getX12278SubID() {
        return x12278SubID;
    }

    public void setX12278SubID(String x12278SubID) {
        this.x12278SubID = x12278SubID;
    }
    */
    //private String x12278SubID;

    public long getX12278SubID() {
        return x12278SubID;
    }

    public void setX12278SubID(long x12278SubID) {
        this.x12278SubID = x12278SubID;
    }

    private long x12278SubID;
    private String paMessage;
    private String paUTN;

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRcverID() {
        return rcverID;
    }

    public void setRcverID(String rcverID) {
        this.rcverID = rcverID;
    }

    private String senderID;
    private String rcverID;
}


