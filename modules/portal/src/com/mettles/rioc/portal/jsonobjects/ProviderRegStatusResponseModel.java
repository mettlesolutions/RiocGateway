package com.mettles.rioc.portal.jsonobjects;

import java.util.ArrayList;

public class ProviderRegStatusResponseModel {
    private ArrayList<ErrorJsonObject> errors;
    private ArrayList<StatusChangesJsonObject> status_changes;
    private String provider_name;
    private String provider_id;

    public String getCall_error_code() {
        return call_error_code;
    }

    public void setCall_error_code(String call_error_code) {
        this.call_error_code = call_error_code;
    }

    private String call_error_code;

    public String getCall_error_description() {
        return call_error_description;
    }

    public void setCall_error_description(String call_error_description) {
        this.call_error_description = call_error_description;
    }

    private String call_error_description;

    public ArrayList<ErrorJsonObject> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<ErrorJsonObject> errors) {
        this.errors = errors;
    }

    public ArrayList<StatusChangesJsonObject> getStatus_changes() {
        return status_changes;
    }

    public void setStatus_changes(ArrayList<StatusChangesJsonObject> status_changes) {
        this.status_changes = status_changes;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getProvider_street() {
        return provider_street;
    }

    public void setProvider_street(String provider_street) {
        this.provider_street = provider_street;
    }

    public String getProvider_street2() {
        return provider_street2;
    }

    public void setProvider_street2(String provider_street2) {
        this.provider_street2 = provider_street2;
    }

    public String getProvider_city() {
        return provider_city;
    }

    public void setProvider_city(String provider_city) {
        this.provider_city = provider_city;
    }

    public String getProvider_state() {
        return provider_state;
    }

    public void setProvider_state(String provider_state) {
        this.provider_state = provider_state;
    }

    public String getProvider_zip() {
        return provider_zip;
    }

    public void setProvider_zip(String provider_zip) {
        this.provider_zip = provider_zip;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmission_status() {
        return submission_status;
    }

    public void setSubmission_status(String submission_status) {
        this.submission_status = submission_status;
    }

    public String getTransaction_id_list() {
        return transaction_id_list;
    }

    public void setTransaction_id_list(String transaction_id_list) {
        this.transaction_id_list = transaction_id_list;
    }

    private String provider_street;
    private String provider_street2;
    private String provider_city;
    private String provider_state;
    private String provider_zip;
    private String stage;
    private String status;
    private String submission_status;
    private String transaction_id_list;

}
