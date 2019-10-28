package com.mettles.rioc.core;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.Metadata;
import com.mettles.esMDConnectLib.StatusInfo;
import com.mettles.esMDConnectLib.X12BatchTimeConnectSoapCall;
import com.mettles.esMDConnectLib.X12Submission;
import com.mettles.rioc.X12275SubmissionStatus;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.x12.EntityIdentityInfo;
import com.mettles.rioc.x12.X12275314EDIWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;


import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mettles.rioc.x12.UnSolicitedPWKElems;

@Component(X12275ConnectSoapClient.NAME)
public class X12275ConnectSoapClient {
    public static final String NAME = "rioc_X12275ConnectSoapClient";
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

    public X12275SubmissionStatus SubmitCAQHBatchSubmission(Submission sub){
        X12275SubmissionStatus retVal = new X12275SubmissionStatus();
        List<HIHConfiguration> hihConfigList = datamanager.load(HIHConfiguration.class)
                .query("select p from rioc_HIHConfiguration p")
                .view("HIHConfig-view")
                .list();
        String HIH_Name = "Mettle Solutions Test HIH";
        String HIH_Oid="2.16.840.1.113883..6037.2";
        String HIH_Description = "Test Hih Oid to create the data";
        String HIH_Esmd_Url = "https://207.37.82.83:443/Gateway/DocumentSubmission/1_1/DocumentRepositoryXDR_Service";
        String Issuer = "cn=val.mettles.com, o=\"Mettle Solutions, LLC.\", l=Columbia, st=Maryland, c=US";
        String senderediID = "360372";
        if(hihConfigList == null){
            System.out.println("HIHConfiguration parameters are not set");
            retVal.setErrorCode("Failure");
            retVal.setErrorMessage("HIH Config Parameters are not set");
            return retVal;

        }else if(hihConfigList.size() >0){
            Iterator<HIHConfiguration> hihConfigurationIterator = hihConfigList.iterator();
            if(hihConfigurationIterator.hasNext()){
                HIHConfiguration hihConfigParam = hihConfigurationIterator.next();
                HIH_Name = hihConfigParam.getHiHName();
                HIH_Oid = hihConfigParam.getHIIHOid();
                HIH_Description = hihConfigParam.getHIHDescription();
                HIH_Esmd_Url = hihConfigParam.getEsMDUrl();
                Issuer = hihConfigParam.getIssuer();
                senderediID = hihConfigParam.getEdiID();
            }

        }

        String ediPayload = GenerateX12275EDI(sub,HIH_Name, HIH_Oid,senderediID);

        SplitMaps newSplitMap ;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            newSplitMap = metadata.create(SplitMaps.class);
            newSplitMap.setSubmissionId(sub);
            newSplitMap.setSubmissionUniqueId(newSplitMap.getId().toString());
            newSplitMap.setSplitSubmissionStatus("submitted");
            newSplitMap.setSplitInformation(1);
            em.persist(newSplitMap);
            tx.commit();
        }

        if(newSplitMap != null) {

            sub.setUniqueIdList(newSplitMap.getId().toString());
            retVal.setUniqueID(sub.getUniqueIdList());

        }else{
            System.out.println("splitmap is null");
            retVal.setErrorCode("Failure");
            retVal.setErrorMessage("Unable to create Message");
            return retVal;
        }

        X12Submission x12metadata = new X12Submission();
        x12metadata.setEsmd_Url(HIH_Esmd_Url);
        x12metadata.setHih_Desp(HIH_Description);
        x12metadata.setHih_Name(HIH_Name);
        x12metadata.sethIHOID(HIH_Oid);
        x12metadata.setIssuer(Issuer);
        x12metadata.setProviderNPI(sub.getAuthorNPI());
        x12metadata.setRcOid(sub.getIntendedRecepient().getOid());
        X12BatchTimeConnectSoapCall x12connctclnt = new X12BatchTimeConnectSoapCall();
        StatusInfo status = x12connctclnt.BatchConnectSoapClient(x12metadata,newSplitMap.getId().toString(), ediPayload,"http://localhost:8080");
        retVal.setErrorMessage(status.getResult());
        retVal.setErrorCode(status.getStatus());

        return retVal;
    }

    public String GenerateX12275EDI(Submission sub,String hihName, String hihOid,String senderEDID){
        X12275314EDIWriter x12275pwk = new X12275314EDIWriter();
        UnSolicitedPWKElems temp = new UnSolicitedPWKElems();
        EntityIdentityInfo payer = new EntityIdentityInfo();
        payer.setEntityLastName(sub.getIntendedRecepient().getIntendedRecepient());
        payer.setEntityNPI("10012");
        EntityIdentityInfo submitter = new EntityIdentityInfo();
        submitter.setEntityEntityTypeQualifier("2");
        submitter.setEntityLastName(hihName);
        submitter.setEntityNPI(hihOid);
        EntityIdentityInfo provider = new EntityIdentityInfo();
        if(sub.getAuthorType().getId().equals("Institution")) {
            provider.setEntityEntityTypeQualifier("2");
            provider.setEntityLastName("Test Organization Inc");
        }
        else{
            provider.setEntityEntityTypeQualifier("1");
            provider.setEntityLastName("Test");
            provider.setEntityFirstName("Provider");
        }

        provider.setEntityNPI(sub.getAuthorNPI());
        provider.setEntityAddressLine1("10320 Little Patuxent Pkwy");
        provider.setEntityCity("Columbia");
        provider.setEntityState("MD");
        provider.setEntityZIPCode("21044");
        EntityIdentityInfo patient = new EntityIdentityInfo();
        patient.setEntityLastName("LastName");
        patient.setEntityFirstName("FirstName");
        patient.setEntityNPI("HCN1234567");
        temp.setPayer(payer);
        temp.setPatient(patient);
        temp.setProvider(provider);
        temp.setSubmitter(submitter);
        temp.setEsMDClaimID(sub.getEsMDClaimID());

        temp.setAttctrlNum(sub.getDocument().iterator().next().getAttachmentControlNumber());
        Iterator<Document> documentIterator = sub.getDocument().iterator();
        while(documentIterator.hasNext()){
            Document dtTemp = documentIterator.next();
            String filetype = DOCUMENT_TYPE;
            if(dtTemp.getDocument_type() == DocumentType.PDF)
                filetype = DOCUMENT_TYPE;
            else if(dtTemp.getDocument_type() == DocumentType.XML)
                filetype = DOCUMENT_TYPE_XML;

            String fileStr ="";
            try {
                InputStream inputStream = fileLoader.openStream(dtTemp.getFileDescriptor());
                fileStr =   Base64.encodeBase64String(IOUtils.toByteArray(inputStream));
                temp.getB64PayloadList().add(Base64.encodeBase64String(DocumentPrepare.getContent(fileStr,filetype).getBytes()));
                inputStream.close();
            }catch(Exception e){
                System.out.println("Exception occured while reading the file");
            }
        }

       // sub.getDocument().forEach(item->{ InputStream inputStream = fileLoader.openStream(item.getFileDescriptor());})
        return x12275pwk.CreateEDI(temp,senderEDID,sub.getIntendedRecepient().getEdiID());
    }
}