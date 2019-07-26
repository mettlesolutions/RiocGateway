package com.mettles.rioc.portal.jsonobjects;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

public class PostSubmissionRequest implements Serializable {
    public List<MultipartFile> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<MultipartFile> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    private List<MultipartFile> uploadFiles;

}
