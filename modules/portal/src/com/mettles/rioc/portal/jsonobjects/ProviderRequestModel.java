package com.mettles.rioc.portal.jsonobjects;

public class ProviderRequestModel {

    private String provider_name;
    private String provider_npi;

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getProvider_npi() {
        return provider_npi;
    }

    public void setProvider_npi(String provider_npi) {
        this.provider_npi = provider_npi;
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

    private String provider_street;
    private String provider_street2;
    private String provider_city;
    private String provider_state;
    private String provider_zip;
}
