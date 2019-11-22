package com.mettles.rioc.portal.jsonobjects;

import java.io.Serializable;

public class X12278CreateSubmissionResponseModel implements Serializable {

    public String getX12278submission_id() {
        return x12278submission_id;
    }

    public void setX12278submission_id(String x12278submission_id) {
        this.x12278submission_id = x12278submission_id;
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

    private String call_error_code;
    private String call_error_description;
    private String x12278submission_id;
}
