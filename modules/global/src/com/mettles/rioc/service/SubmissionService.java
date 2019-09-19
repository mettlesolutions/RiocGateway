package com.mettles.rioc.service;


import com.mettles.rioc.entity.Document;
import com.mettles.rioc.entity.Providers;
import com.mettles.rioc.entity.Submission;
import com.mettles.rioc.X12275SubmissionStatus;
import com.haulmont.cuba.core.entity.FileDescriptor;

import java.util.ArrayList;
import java.util.List;

public interface SubmissionService {
    String NAME = "rioc_SubmissionService";


    public Submission CallConnectApi(Submission sub, String SplitStr, String parentId, ArrayList<Document> docArryList);
    public List<Document> SplitDocuments(Document largeDoc);
    public Submission SubmiteMDRRegFile(Providers prov , boolean bRegister);
    public X12275SubmissionStatus SubmitX12275Submission(Submission sub);

    public void PDFReadWrite();
}