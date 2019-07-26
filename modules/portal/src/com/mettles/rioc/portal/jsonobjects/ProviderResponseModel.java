package com.mettles.rioc.portal.jsonobjects;

public class ProviderResponseModel {

    private String provider_id;

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getProvider_status() {
        return provider_status;
    }

    public void setProvider_status(String provider_status) {
        this.provider_status = provider_status;
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

    private String provider_status;
    private String call_error_code;
    private String call_error_description;
}
