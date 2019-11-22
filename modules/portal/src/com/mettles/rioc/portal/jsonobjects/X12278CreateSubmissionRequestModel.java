package com.mettles.rioc.portal.jsonobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class X12278CreateSubmissionRequestModel implements Serializable {

    public String getExcel_file_name() {
        return excel_file_name;
    }

    public void setExcel_file_name(String excel_file_name) {
        this.excel_file_name = excel_file_name;
    }

    public String getIntended_recepient() {
        return intended_recepient;
    }

    public void setIntended_recepient(String intended_recepient) {
        this.intended_recepient = intended_recepient;
    }

    public ArrayList<PaDocumentSetJsonObject> getPa_document() {
        return pa_document;
    }

    public void setPa_document(ArrayList<PaDocumentSetJsonObject> pa_document) {
        this.pa_document = pa_document;
    }

    private ArrayList<PaDocumentSetJsonObject> pa_document;
    private String excel_file_name;
    private String intended_recepient;
}
