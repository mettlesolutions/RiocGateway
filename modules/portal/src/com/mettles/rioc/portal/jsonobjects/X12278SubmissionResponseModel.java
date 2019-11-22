package com.mettles.rioc.portal.jsonobjects;

import com.mettles.rioc.ErrorInfo;
import com.mettles.rioc.StatusInfo;

import java.util.ArrayList;
import java.util.Hashtable;

public class X12278SubmissionResponseModel {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
    }

    public String getSUPP_DOC_MESSAGE() {
        return SUPP_DOC_MESSAGE;
    }

    public void setSUPP_DOC_MESSAGE(String SUPP_DOC_MESSAGE) {
        this.SUPP_DOC_MESSAGE = SUPP_DOC_MESSAGE;
    }

    public String getDoc_Unq_id() {
        return doc_Unq_id;
    }

    public void setDoc_Unq_id(String doc_Unq_id) {
        this.doc_Unq_id = doc_Unq_id;
    }

    public String getSuppDoc_esmd_transaction_id() {
        return suppDoc_esmd_transaction_id;
    }

    public void setSuppDoc_esmd_transaction_id(String suppDoc_esmd_transaction_id) {
        this.suppDoc_esmd_transaction_id = suppDoc_esmd_transaction_id;
    }

    /*
    public String getPA_MESSAGE() {
        return PA_MESSAGE;
    }

    public void setPA_MESSAGE(String PA_MESSAGE) {
        this.PA_MESSAGE = PA_MESSAGE;
    }

    public String getPA_UTN() {
        return PA_UTN;
    }

    public void setPA_UTN(String PA_UTN) {
        this.PA_UTN = PA_UTN;
    }
    */

    public String getCall_error_code() {
        return call_error_code;
    }

    public void setCall_error_code(String call_error_code) {
        this.call_error_code = call_error_code;
    }

    public String getCall_error_description() {
        return call_error_description;
    }

    public void setCall_error_description(String call_error_description) {
        this.call_error_description = call_error_description;
    }

    public String getX12278submission_id() {
        return x12278submission_id;
    }

    public void setX12278submission_id(String x12278submission_id) {
        this.x12278submission_id = x12278submission_id;
    }

    /*
    public Hashtable<String, String> getError999Key() {
        return error999Key;
    }

    public void setError999Key(Hashtable<String, String> error999Key) {
        this.error999Key = error999Key;
    }
    */
    /*

    public ArrayList<ErrorInfo> getSegErrs() {
        return segErrs;
    }

    public void setSegErrs(ArrayList<ErrorInfo> segErrs) {
        this.segErrs = segErrs;
    }
    */

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    /*
    public ArrayList<StatusInfo> getSegStatus() {
        return segStatus;
    }

    public void setSegStatus(ArrayList<StatusInfo> segStatus) {
        this.segStatus = segStatus;
    }
    */

    public String getError_code_context() {
        return error_code_context;
    }

    public void setError_code_context(String error_code_context) {
        this.error_code_context = error_code_context;
    }

    public ArrayList<X12278ErrorJsonObject> getErrJsonObjs() {
        return errJsonObjs;
    }

    public void setErrJsonObjs(ArrayList<X12278ErrorJsonObject> errJsonObjs) {
        this.errJsonObjs = errJsonObjs;
    }


    public ArrayList<X12278StatusChangeJsonObject> getStaChngJsonObjs() {
        return staChngJsonObjs;
    }

    public void setStaChngJsonObjs(ArrayList<X12278StatusChangeJsonObject> staChngJsonObjs) {
        this.staChngJsonObjs = staChngJsonObjs;
    }

    private ArrayList<X12278StatusChangeJsonObject> staChngJsonObjs;
    private ArrayList<X12278ErrorJsonObject> errJsonObjs;
    private String error_code_context;
    private String transType;
    //private ArrayList<ErrorInfo> segErrs;
    //private ArrayList<StatusInfo> segStatus;
    //private Hashtable<String, String> error999Key = new Hashtable<String, String>();
    private String x12278submission_id;
    private String call_error_code;
    private String call_error_description;
    private String status;
    private String esmd_transaction_id;
    private String SUPP_DOC_MESSAGE;
    private String doc_Unq_id;
    private String suppDoc_esmd_transaction_id;
    //private String PA_MESSAGE;
    //private String PA_UTN;


}
