package com.mettles.rioc.core;


import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.mettles.esMDConnectLib.*;
import com.mettles.rioc.entity.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Error;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component(X12278DocSubmissionClient.NAME)
public class X12278DocSubmissionClient {

    public static final String NAME = "rioc_X12278DocSubmissionClient";
    private static final String DOCUMENT_TYPE = "application/pdf";
    private static final String DOCUMENT_TYPE_XML = "application/xml";

    @Inject
    FileLoader fileLoader;
    @Inject
    DataManager datamanager;
    @Inject
    Persistence persistence;
    @Inject
    Metadata metadata;


    public SubmissionStatus SoapClientCall(X12278Submission x12sub, DocSubmissionData subdata, String docUnqID) {
        // String url = "http://val.mettles.com:8080/Adapter/esmd/AdapterService/AdapterDocSubmissionDeferredRequest";
        int retVal = -1;

        SubmissionStatus substatus = new SubmissionStatus();
        //client.enableMtom();
        try {
            ConnectSoapClient soapClient = new ConnectSoapClient();
            substatus = soapClient.SoapClientCall(subdata,docUnqID,null,"http://localhost:8080");


            /*
            PAStatusChange statChng = new PAStatusChange();
            statChng.setResult(substatus.getStatchng().getResult());
            statChng.setStatus(substatus.getStatchng().getStatus());
            statChng.setX12278submissionID(x12sub);
            //statChng.setSplitInformation(Integer.toString(SplitNum));
            x12sub.getPaStatusChange().add(statChng);

            if(substatus.getStatus() != 0){
                System.out.println("Error occured"+substatus.getStatus());
                if(substatus.getError() != null){
                    Iterator<ErrorInfo> errinfoIt = substatus.getError().iterator();
                    List<PAError> temperrList = x12sub.getPaError();
                    if(temperrList == null){
                        temperrList = new ArrayList<>();
                        x12sub.setPaError(temperrList);
                    }
                    while(errinfoIt.hasNext()){
                        PAError tempErr = new PAError();
                        ErrorInfo errInfo = errinfoIt.next();
                        tempErr.setErrorCode(errInfo.getErrorCode());
                        tempErr.setError(errInfo.getValue());
                        tempErr.setCodeContext(errInfo.getCodeContext());
                        tempErr.setSeverity(errInfo.getSeverity());
                        //tempErr.setSplitInformation(SplitNum);
                        tempErr.setX12278submissionID(x12sub);
                        temperrList.add(tempErr);
                    }

                }
            }
            */
            // String str = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:RequestAccepted";


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return substatus;


    }

}