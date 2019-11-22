package com.mettles.rioc.portal.jsonobjects;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

public class PostX12278SubmissionRequest implements Serializable  {

    public List<MultipartFile> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<MultipartFile> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public MultipartFile getExcel_file() {
        return excel_file;
    }

    public void setExcel_file(MultipartFile excel_file) {
        this.excel_file = excel_file;
    }

    private MultipartFile excel_file;
    private List<MultipartFile> uploadFiles;
}
