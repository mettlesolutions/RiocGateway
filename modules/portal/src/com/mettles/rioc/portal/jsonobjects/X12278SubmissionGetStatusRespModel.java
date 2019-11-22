package com.mettles.rioc.portal.jsonobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class X12278SubmissionGetStatusRespModel implements Serializable {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submission_id) {
        this.submission_id = submission_id;
    }

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

    public ArrayList<X12278ErrorJsonObject> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<X12278ErrorJsonObject> errors) {
        this.errors = errors;
    }

    public ArrayList<X12278StatusChangeJsonObject> getStatus_changes() {
        return status_changes;
    }

    public void setStatus_changes(ArrayList<X12278StatusChangeJsonObject> status_changes) {
        this.status_changes = status_changes;
    }

    public String getEsmd_transaction_id() {
        return esmd_transaction_id;
    }

    public void setEsmd_transaction_id(String esmd_transaction_id) {
        this.esmd_transaction_id = esmd_transaction_id;
    }

    public String getDocument_submission_status() {
        return document_submission_status;
    }

    public void setDocument_submission_status(String document_submission_status) {
        this.document_submission_status = document_submission_status;
    }

    public String getDocument_unique_id() {
        return document_unique_id;
    }

    public void setDocument_unique_id(String document_unique_id) {
        this.document_unique_id = document_unique_id;
    }

    public String getDocument_esmd_transaction_id() {
        return document_esmd_transaction_id;
    }

    public void setDocument_esmd_transaction_id(String document_esmd_transaction_id) {
        this.document_esmd_transaction_id = document_esmd_transaction_id;
    }

    private String esmd_transaction_id;
    private String document_submission_status;
    private String document_unique_id;
    private String document_esmd_transaction_id;
    private String status;
    private String submission_id;
    private String call_error_code;
    private String call_error_description;
    private ArrayList<X12278ErrorJsonObject> errors;
    private ArrayList<X12278StatusChangeJsonObject> status_changes;
}
