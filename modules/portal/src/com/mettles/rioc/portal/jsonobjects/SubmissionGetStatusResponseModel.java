package com.mettles.rioc.portal.jsonobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class SubmissionGetStatusResponseModel implements Serializable {
    private String stage;
    private String status;
    private String submission_id;

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

    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submission_id) {
        this.submission_id = submission_id;
    }

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

    private ArrayList<ErrorJsonObject> errors;
    private ArrayList<StatusChangesJsonObject> status_changes;

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

}
