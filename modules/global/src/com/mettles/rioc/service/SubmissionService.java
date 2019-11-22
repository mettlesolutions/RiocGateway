package com.mettles.rioc.service;


import com.mettles.rioc.X12278SubmissionStatus;
import com.mettles.rioc.X12278ValidationStatus;
import com.mettles.rioc.entity.Document;
import com.mettles.rioc.entity.Providers;
import com.mettles.rioc.entity.Submission;
import com.mettles.rioc.X12275SubmissionStatus;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.mettles.rioc.entity.X12278Submission;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public interface SubmissionService {
    String NAME = "rioc_SubmissionService";


    public Submission CallConnectApi(Submission sub, String SplitStr, String parentId, ArrayList<Document> docArryList);
    public List<Document> SplitDocuments(Document largeDoc);
    public Submission SubmiteMDRRegFile(Providers prov , boolean bRegister);
    public X12275SubmissionStatus SubmitX12275Submission(Submission sub);
    public X12278SubmissionStatus SubmitX12278Submission(X12278Submission x12sub, String EDI, Hashtable <String, String> err999Key);
    public X12278ValidationStatus ParseHashtable(Hashtable<String,String> input, String rcvrEDI, String requesterNPI);
    public void PDFReadWrite();
}