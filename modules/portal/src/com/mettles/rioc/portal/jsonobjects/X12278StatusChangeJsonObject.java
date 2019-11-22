package com.mettles.rioc.portal.jsonobjects;

public class X12278StatusChangeJsonObject {
    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    private String esmd_transaction_id;
    private String status;
    private String time;
    private String result;
    private String trans_type;


}
