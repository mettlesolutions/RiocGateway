package com.mettles.rioc.portal.jsonobjects;

import java.io.Serializable;

public class SubmissionResponseModel implements Serializable {

    private String submission_id;

    private String submission_status;
    private String call_error_code;
    private String call_error_description;


    public String getSubmission_id() {
        return submission_id;
    }

    public void setSubmission_id(String submission_id) {
        this.submission_id = submission_id;
    }

    public String getSubmission_status() {
        return submission_status;
    }

    public void setSubmission_status(String submission_status) {
        this.submission_status = submission_status;
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



}
