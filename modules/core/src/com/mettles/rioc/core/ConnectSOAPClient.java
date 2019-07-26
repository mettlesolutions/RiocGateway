package com.mettles.rioc.core;


import com.mettles.rioc.BeanNotificationEvent;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.entity.Error;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.RefreshToken;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.app.Authenticated;
import gov.cms.esmd.schemas.v2.serviceregistration.*;
import gov.cms.esmd.schemas.v2.serviceregistration.ActionType;
import gov.hhs.fha.nhinc.common.nhinccommon.*;
import gov.hhs.fha.nhinc.common.nhinccommon.PersonNameType;
import gov.hhs.fha.nhinc.common.nhinccommon.UserType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;


//import org.apache.axis2.databinding.utils.ConverterUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType;
//import gov.hhs.fha.nhinc.docsubmission.adapter.component.proxy.AdapterComponentXDRPortType;
import gov.hhs.fha.nhinc.adaptercomponentxdrrequest.AdapterComponentXDRRequestPortType;
import gov.hhs.fha.nhinc.docsubmission.adapter.component.deferred.request.proxy.service.AdapterComponentDocSubmissionRequestServicePortDescriptor;
//import gov.hhs.fha.nhinc.docsubmission.adapter.component.proxy.service.AdapterComponentDocSubmissionServicePortDescriptor;
import gov.hhs.fha.nhinc.messaging.client.CONNECTCXFClientFactory;
import gov.hhs.fha.nhinc.messaging.client.CONNECTClient;
import gov.hhs.fha.nhinc.messaging.service.port.ServicePortDescriptor;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(ConnectSOAPClient.NAME)
@SuppressWarnings("unchecked")
public class ConnectSOAPClient {
    public static final String NAME = "rioc_ConnectSOAPClient";
    public static final String DOCUMENT_TYPE = "application/pdf";
    public static final String DOCUMENT_TYPE_XML = "application/xml";
    public static final String DOCUMENT_OBJECTTYPE = "urn:uuid:7edca82f-054d47f2-a032-9b2a5b5186c1";
    public static final String DOC_CLASSIFICATION_ID1="cl15";
    public static final String DOC_CLASSIFICATION_ID_SCHEME1="urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION1="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_FORMAT_CODE_TYPE="Scanned PDF Document in Clinical Document Architecture (CDA) C62 Construct";
    public static final String DOC_CLASSIFICATION_ID2="cl01";
    public static final String DOC_CLASSIFICATION_ID_SCHEME2="urn:uuid:93606bcf-9494-43ec-9b4ea7748d1a838d";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION2="author";
    public static final String DOC_CLASSIFICATION_ID3="cl02";
    public static final String DOC_CLASSIFICATION_ID_SCHEME3="urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION3="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_CLASS_CODE_TYPE="Unstructured Document Submission";
    public static final String DOC_CLASSIFICATION_ID4="cl03";
    public static final String DOC_CLASSIFICATION_ID_SCHEME4="urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION4="2.16.840.1.113883.5.25";
    public static final String DOC_CONFIDENTIALITY_CODE_TYPE="VeryRestricted";
    public static final String DOC_CLASSIFICATION_ID5="cl06";
    public static final String DOC_CLASSIFICATION_ID_SCHEME5="urn:uuid:cccf5598-8b07-4b77-a05eae952c785ead";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION5="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_PRACSETTINGS_CODE_TYPE="NA";
    public static final String DOC_CLASSIFICATION_ID6="cl05";
    public static final String DOC_CLASSIFICATION_ID_SCHEME6="urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION6="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_HEALTHCAREFACILITY_CODE_TYPE="Health Information Handler (HIH)";
    public static final String DOC_CLASSIFICATION_ID7="cl07";
    public static final String DOC_CLASSIFICATION_ID_SCHEME7="urn:uuid:f0306f51-975f-434e-a61cc59651d33983";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION7="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_CODINGSCHEME_CODE_TYPE="Outpatient Evaluation And Management";
    public static final String DOC_CLASSIFICATION_ID8="cl08";
    public static final String DOC_CLASSIFICATION_ID_SCHEME8="urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
    public static final String DOC_CLASSIFICATION_ID_NODE_REPRESENTATION8="2.16.840.1.113883.13.34.110.1.1000.1";
    public static final String DOC_CLASSCODE_TYPE="Unstructured Document Submission";
    public static final String DOC_HASH_STRING = "ad18814418693512b767676006a21d8ec7291e84";
    public static final String DOC_EXTROBJ_NAME = "Claim Supporting Medical Documentation";

    @Inject
    FileLoader fileLoader;
    @Inject
    DataManager datamanager;
    @Inject
    Persistence persistence;
    @Inject
    Metadata metadata;

    @Authenticated
    @EventListener
    public void onBeanNotificationEvent(BeanNotificationEvent event) {
        String uniqueId = event.getMessage();
        System.out.println("Received BeanNotification Event"+uniqueId);
        SplitMaps splitmaps = datamanager.load(SplitMaps.class).id(UUID.fromString(uniqueId)).view("splitmap-view").one();
        if(splitmaps != null){
            Submission sub = splitmaps.getSubmissionId();
            if(sub.getEsmdTransactionId() != null){
                String currTransId = sub.getEsmdTransactionId();
                String transIdList = sub.getTransactionIdList();
                if(transIdList != null){
                   if(transIdList.lastIndexOf(currTransId) == -1){
                       transIdList = transIdList + ","+currTransId;
                   }
                }else{
                    transIdList = currTransId;
                }
                sub.setTransactionIdList(transIdList);
                datamanager.commit(sub);
            }
            if(splitmaps.getSplitSubmissionStatus().equals("esMD - Delivery To Enterprise File Transfer"))
            {


                if(sub.getHighestSpiltNo() == splitmaps.getSplitInformation())
                {
                    System.out.println("Completed sending all the splits");
                }else{
                    List<Document> subArryList = sub.getDocument();
                    int splitNum = sub.getLastSubmittedSplit()+1;
                    subArryList.sort(Comparator.comparing(Document::getSplitNumber));
                    Dictionary<Integer,ArrayList<Document>> splitDocDict = new Hashtable<>();
                    for (Document doc : subArryList) {
                        int currSplitNum = doc.getSplitNumber();
                        if(((Hashtable<Integer, ArrayList<Document>>) splitDocDict).containsKey(currSplitNum)){
                            ArrayList<Document> docArryList = splitDocDict.get(currSplitNum);
                            docArryList.add(doc);
                            splitDocDict.put(currSplitNum,docArryList);
                        }else{
                            ArrayList<Document> docArryList = new ArrayList<>();
                            docArryList.add(doc);
                            splitDocDict.put(currSplitNum,docArryList);
                        }
                    }
                    if((splitNum > 1) && (splitNum != splitmaps.getSplitInformation())){
                        int beforeSubErrlistSize = sub.getError().size();
                        int beforeSubStatChng = sub.getStatusChange().size();
                        int numofSplits = ((Hashtable<Integer, ArrayList<Document>>) splitDocDict).keySet().size();
                        String splitStr = splitNum +"-"+numofSplits;
                        try {
                            SoapClientCall(sub, splitStr, splitmaps.getSubmissionUniqueId(), splitDocDict.get(splitNum));
                            int AfterSubErrlistSize = sub.getError().size();
                            int AfterSubStatChng = sub.getStatusChange().size();
                            System.out.println("Updated Last Submitted Split " + splitNum);
                            sub.setLastSubmittedSplit(splitNum);
                            datamanager.commit(sub);
                            System.out.println("stat change size before" + beforeSubStatChng + "Stat Change After" + AfterSubStatChng);
                            System.out.println("Error change size before" + beforeSubErrlistSize + "Error Change After" + AfterSubErrlistSize);


                        }catch(Exception e){
                            e.printStackTrace();
                        }

                            List<Document> dtArryList = splitDocDict.get(splitNum);
                            Iterator<Document> dtIterator = dtArryList.iterator();
                            CommitContext dtRemoveCtx = new CommitContext();

                            while(dtIterator.hasNext()){
                                Document dtTemp = dtIterator.next();
                                //  datamanager.remove(dtTemp.getFileDescriptor());
                                dtRemoveCtx.addInstanceToRemove(dtTemp.getFileDescriptor());
                                dtTemp.setFileDescriptor(null);
                                try {
                                    fileLoader.removeFile(dtTemp.getFileDescriptor());
                                }catch(Exception e){
                                    System.out.println("Exception Occured while removing the file");
                                }


                            }
                            datamanager.commit(dtRemoveCtx);





                    }

                }
            }

        }else{
            System.out.println("splitmaps is null");
        }

    }

    public Submission SoapClientCall(Submission sub, String SplitStr, String parentId, ArrayList<Document> docArryList) throws PropertyException{
        String url = "http://val.mettles.com:8080/Adapter/esmd/AdapterService/AdapterDocSubmissionDeferredRequest";
        int retVal = -1;
        List <HIHConfiguration> hihConfigList = datamanager.load(HIHConfiguration.class)
                .query("select p from rioc_HIHConfiguration p")
                .view("HIHConfig-view")
                .list();
        String HIH_Name = "Mettle Solutions Test HIH";
        String HIH_Oid="2.16.840.1.113883.3.6037.2";
        String HIH_Description = "Test Hih Oid to create the data";
        String HIH_Esmd_Url = "https://207.37.82.83:443/Gateway/DocumentSubmission/1_1/DocumentRepositoryXDR_Service";
        if(hihConfigList == null){
            System.out.println("HIHConfiguration parameters are not set");

         }else if(hihConfigList.size() >0){
            Iterator<HIHConfiguration> hihConfigurationIterator = hihConfigList.iterator();
            if(hihConfigurationIterator.hasNext()){
                HIHConfiguration hihConfigParam = hihConfigurationIterator.next();
                HIH_Name = hihConfigParam.getHiHName();
                HIH_Oid = hihConfigParam.getHIIHOid();
                HIH_Description = hihConfigParam.getHIHDescription();
                HIH_Esmd_Url = hihConfigParam.getEsMDUrl();
            }

        }
        ProvideAndRegisterDocumentSetRequestType msg = new ProvideAndRegisterDocumentSetRequestType();

        AdapterProvideAndRegisterDocumentSetRequestType type = new AdapterProvideAndRegisterDocumentSetRequestType();

        AdapterProvideAndRegisterDocumentSetRequestType request = new AdapterProvideAndRegisterDocumentSetRequestType();
        //-------------------------///
        int SplitNum = docArryList.iterator().next().getSplitNumber();
        SplitMaps newSplitMap = null;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            newSplitMap = metadata.create(SplitMaps.class);
            newSplitMap.setSubmissionId(sub);
            if(parentId == null){
                newSplitMap.setSubmissionUniqueId(newSplitMap.getId().toString());
                parentId = newSplitMap.getId().toString();
            }else {
                newSplitMap.setSubmissionUniqueId(parentId);
            }
            newSplitMap.setSplitSubmissionStatus("submitted");
            newSplitMap.setSplitInformation(SplitNum);
            em.persist(newSplitMap);
            tx.commit();
        }
        String uniqueid = null;

        if(newSplitMap != null) {
             uniqueid = newSplitMap.getId().toString();
            if(sub.getUniqueIdList() == null)
               sub.setUniqueIdList(uniqueid);
            else
                sub.setUniqueIdList(sub.getUniqueIdList()+","+uniqueid);
        }else{
            System.out.println("splitmap is null");
        }

        SubmitObjectsRequest sor = new SubmitObjectsRequest();
        sor.setId("999");
        sor.setComment("esMD Claim Document Submission in response to Review Contractor ADR Letter");

        RegistryObjectListType rol = new RegistryObjectListType();

          System.out.println("Entered connect call");
        ExtrinsicObjectType eot = new ExtrinsicObjectType();
        RegistryPackageType rpt = new RegistryPackageType();
        rpt.setId("SubmissionSet01");


        rpt.getSlot().add(createSlotIndividual("esMDClaimId",sub.getEsMDClaimID()));
        rpt.getSlot().add(createSlotIndividual("esMDCaseId",sub.getEsmdCaseId()));
        rpt.getSlot().add(createSlotIndividual("intendedRecipient",sub.getIntendedRecepient().getOid()));
        if(!SplitStr.equals("1-1")){
            rpt.getSlot().add(createSlotIndividual("parentUniqueNumber","_"+parentId));
            rpt.getSlot().add(createSlotIndividual("splitNumber",SplitStr));
        }
        System.out.println("Entered connect call1");
        ClassificationType tempcls = new ClassificationType();
        tempcls.setId("888");
        tempcls.setClassifiedObject("SubmissionSet01");
        tempcls.setClassificationNode("urn:uuid:"+UUID.randomUUID().toString());
        JAXBElement<ClassificationType> jaxbElement0 =
                new JAXBElement( new QName("Classification"), ClassificationType.class, null);
        jaxbElement0.setValue(tempcls);
        rol.getIdentifiable().add(jaxbElement0);
        JAXBElement<RegistryPackageType> jaxbElement =
                new JAXBElement( new QName("RegistryPackage"), RegistryPackageType.class, null);
        jaxbElement.setValue(rpt);
        rol.getIdentifiable().add(jaxbElement);



        //rpt.getClassification().add(tempcls);

        ExternalIdentifierType tempextId = new ExternalIdentifierType();
        tempextId.setId("ei03");
        tempextId.setIdentificationScheme("urn:uuid:"+UUID.randomUUID().toString());

        tempextId.setValue("2.16.840.1.113883.13.34.110.1.1000.1^^^&"+sub.getIntendedRecepient().getOid());
        tempextId.setRegistryObject("SubmissionSet01");
        InternationalStringType tempIntLoc = new InternationalStringType();
        LocalizedStringType temploc = new LocalizedStringType();
        temploc.setValue("XDSDocumentEntry.patientId");
        tempIntLoc.getLocalizedString().add(temploc) ;
        tempextId.setName(tempIntLoc);
        rpt.getExternalIdentifier().add(tempextId);
        System.out.println("Entered connect call2");
        ExternalIdentifierType tempextId1 = new ExternalIdentifierType();
        tempextId1.setId("ei04");
        tempextId1.setIdentificationScheme("urn:uuid:"+UUID.randomUUID().toString());
        tempextId1.setValue("12.16.840.1.113883.13.34.110.2");
        tempextId1.setRegistryObject("SubmissionSet01");
        InternationalStringType tempIntLoc1 = new InternationalStringType();
        LocalizedStringType temploc1 = new LocalizedStringType();
        temploc1.setValue("XDSSubmissionSet.sourceId");
        tempIntLoc1.getLocalizedString().add(temploc1) ;
        tempextId1.setName(tempIntLoc1);
        rpt.getExternalIdentifier().add(tempextId1);

        ExternalIdentifierType tempextId2 = new ExternalIdentifierType();
        tempextId2.setId("ei05");
        tempextId2.setIdentificationScheme("urn:uuid:"+uniqueid);
        tempextId2.setValue(uniqueid);
        tempextId2.setRegistryObject("SubmissionSet01");
        InternationalStringType tempIntLoc2 = new InternationalStringType();
        LocalizedStringType temploc2 = new LocalizedStringType();
        temploc2.setValue("XDSSubmissionSet.uniqueId");
        tempIntLoc2.getLocalizedString().add(temploc2) ;
        tempextId2.setName(tempIntLoc2);
        rpt.getExternalIdentifier().add(tempextId2);


        InternationalStringType tempIntLocjaxb = new InternationalStringType();
        LocalizedStringType templocjaxb = new LocalizedStringType();
        System.out.println("Entered connect call3");
        templocjaxb.setValue(sub.getTitle());
        tempIntLocjaxb.getLocalizedString().add(templocjaxb) ;
       rpt.setName(tempIntLocjaxb);

        ClassificationType temp = createClassificationType("cl11","urn:uuid:"+UUID.randomUUID().toString(),"author","SubmissionSet01");
        String authinst = "Institution";
        if(sub.getAuthorType() != null)
            authinst = sub.getAuthorType().getId();

        if(authinst.equals("Institution"))
            temp.getSlot().add(createSlotIndividual("authorInstitution",sub.getAuthorNPI()));
        else
            temp.getSlot().add(createSlotIndividual("authorPerson",sub.getAuthorNPI()));

        rpt.getClassification().add(temp);

        ClassificationType temp1 = createClassificationType("cl09","urn:uuid:aa543740-bdda-424e-8c96-df4873be8500","2.16.840.1.113883.13.34.110.1.1000.1","SubmissionSet01");
        temp1.getSlot().add(createSlotIndividual("contentTypeCode",sub.getPurposeOfSubmission().getContentType()));
        temp1.setName(createInternationalStringType(sub.getPurposeOfSubmission().getPurposeOfSubmission()));

        rpt.getClassification().add(temp1);
        System.out.println("Calling Document Append");
        List<ProvideAndRegisterDocumentSetRequestType.Document> tempDtlist = msg.getDocument();
        //List<Document> doc_set = sub.getDocument();
        Iterator<Document> documentIterator = docArryList.iterator();
        ArrayList<String> filestoDel = new ArrayList<>();
        DocumentPrepare dpTempObj = new DocumentPrepare();
        int idx = 0;

        while(documentIterator.hasNext()){
            Document tempdt  = documentIterator.next();

            JAXBElement<ExtrinsicObjectType> jaxbElement2 =
                    new JAXBElement( new QName("ExtrinsicObject"), ExtrinsicObjectType.class, null);
            jaxbElement2.setValue(CreateExtrinicObject( sub,  tempdt,idx));

            rol.getIdentifiable().add(jaxbElement2);

            ProvideAndRegisterDocumentSetRequestType.Document dt = new ProvideAndRegisterDocumentSetRequestType.Document();
            String docid = "Document",document_id;
            String associd = "as",association_id;
            if(idx<10) {
                document_id = docid + "0" + Integer.toString(idx);
                association_id = associd + "0" + Integer.toString(idx);
            }
            else {
                document_id = docid + Integer.toString(idx);
                association_id =  associd + Integer.toString(idx);
            }


            dt.setId(document_id);

            String fileStr ="";


            try(Transaction tx = persistence.createTransaction()) {

                InputStream inputStream = fileLoader.openStream(tempdt.getFileDescriptor());

                byte fileContent[] = new byte[3000];
                Arrays.fill(fileContent, (byte)0);
                int nRead = 0;
                //while((nRead =inputStream.read(fileContent))  > 0) {
                 //   Base64.
                   //String tempstr =   Base64.encodeBase64String(fileContent);
                 //   fileStr = fileStr + tempstr;
                 //   Arrays.fill(fileContent, (byte)0);
                 //   if(fileStr.length() >= tempdt.getFileDescriptor().getSize())
                  //  {
                //        System.out.println("Completed read length datas"+fileStr.length()+"FD size"+tempdt.getFileDescriptor().getSize());
                    //    break;
                 //   }
               // }
                fileStr =   Base64.encodeBase64String(IOUtils.toByteArray(inputStream));
                tx.close();
                tx.end();

            }catch (FileStorageException | IOException e ) {
                System.out.println("Exception occured while reading file");
                e.printStackTrace();

            }
            System.out.println("Completed read length datas"+fileStr.length()+"FD size"+tempdt.getFileDescriptor().getSize());

            String tempFilepath="";
            System.out.println(fileStr);
            if(tempdt.getDocument_type() == DocumentType.PDF)
                 tempFilepath = dpTempObj.prepareFile(fileStr,DOCUMENT_TYPE);
            else if(tempdt.getDocument_type() == DocumentType.XML)
                tempFilepath = dpTempObj.prepareFile(fileStr,DOCUMENT_TYPE_XML);

            System.out.println(tempFilepath);

            filestoDel.add(tempFilepath);
            FileDataSource fds = new FileDataSource(tempFilepath);


            DataHandler handler = new DataHandler(fds);
            dt.setValue(handler);

            tempDtlist.add(dt);
            idx++;
            JAXBElement<AssociationType1> jaxbElement3 =
                    new JAXBElement( new QName("Association"), AssociationType1.class, null);
            AssociationType1 assocTemp = new AssociationType1();
            jaxbElement3.setValue(assocTemp);
            assocTemp.setId(association_id);
            assocTemp.setAssociationType("HasMember");
            assocTemp.setSourceObject("SubmissionSet01");
            assocTemp.setTargetObject(document_id);
            assocTemp.getSlot().add(createSlotIndividual("SubmissionSetStatus","Original"));

            rol.getIdentifiable().add(jaxbElement3);
        }

        sor.setRegistryObjectList(rol);
        msg.setSubmitObjectsRequest(sor);
        AssertionType assertion = new AssertionType();
        HomeCommunityType hc = new HomeCommunityType();
        hc.setName(HIH_Name);
        hc.setDescription(HIH_Description);
        hc.setHomeCommunityId(HIH_Oid);
        assertion.setHomeCommunity(hc);
        List<String> temppatiendidList = assertion.getUniquePatientId();
        temppatiendidList.add("urn:oid:"+sub.getIntendedRecepient().getOid());
        UserType tempUT = new UserType();
        tempUT.setUserName(sub.getAuthorNPI());
        HomeCommunityType utOrg = new HomeCommunityType();
        utOrg.setHomeCommunityId(sub.getAuthorNPI());
        utOrg.setDescription("HCF");
        utOrg.setName("Health care facility");
        tempUT.setOrg(utOrg);
        PersonNameType personName = new PersonNameType();
        personName.setGivenName(sub.getAuthorNPI());
        personName.setSecondNameOrInitials(sub.getAuthorNPI());
        personName.setFamilyName(sub.getAuthorNPI());
        tempUT.setPersonName(personName);
        CeType rolecoded = new CeType();
        rolecoded.setCode(sub.getAuthorNPI());
        rolecoded.setDisplayName(sub.getAuthorNPI());
        tempUT.setRoleCoded(rolecoded);
        assertion.setUserInfo(tempUT);

        assertion.setNationalProviderId(sub.getAuthorNPI());
        CeType asPurpDisc = new CeType();
        asPurpDisc.setCode("PAYMENT");
        asPurpDisc.setCodeSystem("2.16.840.1.113883.3.18.7.1");
        asPurpDisc.setCodeSystemName("esMD CMS Purpose");
        asPurpDisc.setCodeSystemVersion("1.0");
        asPurpDisc.setDisplayName("Medical Claim Documentation Review");
        asPurpDisc.setOriginalText("Medical Claim Documentation Review");
        assertion.setPurposeOfDisclosureCoded(asPurpDisc);
        SamlAuthnStatementType samAuth = new SamlAuthnStatementType();
        String dtcurrent = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        samAuth.setAuthInstant(dtcurrent+'Z');
        Random rand = new Random();
        int nRandom = rand.nextInt(1001);

        samAuth.setSessionIndex(Integer.toString(nRandom));
        samAuth.setAuthContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");
        samAuth.setSubjectLocalityAddress("localhost");
        samAuth.setSubjectLocalityDNSName("cms.hhs.gov");
        assertion.setSamlAuthnStatement(samAuth);

        SamlAuthzDecisionStatementType samAuthDec = new SamlAuthzDecisionStatementType();
        samAuthDec.setDecision("permit");
        samAuthDec.setResource(HIH_Esmd_Url);
        samAuthDec.setAction(sub.getPurposeOfSubmission().getPurposeOfSubmission());
        SamlAuthzDecisionStatementEvidenceType samDecEvid = new SamlAuthzDecisionStatementEvidenceType();
        SamlAuthzDecisionStatementEvidenceAssertionType samDecEvidAss = new SamlAuthzDecisionStatementEvidenceAssertionType();
        SamlAuthzDecisionStatementEvidenceConditionsType samevicond = new SamlAuthzDecisionStatementEvidenceConditionsType();
        samevicond.setNotBefore(dtcurrent+'Z');
        samevicond.setNotOnOrAfter(dtcurrent+'Z');
        samDecEvidAss.setConditions(samevicond);
        samDecEvidAss.setId(uniqueid);
        samDecEvidAss.setIssueInstant(dtcurrent+'Z');
        samDecEvidAss.setVersion("2.0");
        samDecEvidAss.setIssuerFormat("urn:oasis:names:tc:SAML:1.1:nameidformat:X509SubjectName");
        samDecEvidAss.setIssuer("cn=val.mettles.com, o=\"Mettle Solutions, LLC.\", l=Columbia, st=Maryland, c=US");
        List<String> tempContList = samDecEvidAss.getAccessConsentPolicy();
        tempContList.add(sub.getPurposeOfSubmission().getPurposeOfSubmission());
        List<String> tempinstcontlist = samDecEvidAss.getInstanceAccessConsentPolicy();
        tempinstcontlist.add(sub.getPurposeOfSubmission().getPurposeOfSubmission());
        samDecEvid.setAssertion(samDecEvidAss);
        samAuthDec.setEvidence(samDecEvid);
        assertion.setSamlAuthzDecisionStatement(samAuthDec);
        assertion.setMessageId("urn:oid:"+UUID.randomUUID().toString());

        //-------------------------//
        request.setProvideAndRegisterDocumentSetRequest(msg);

        request.setAssertion(assertion);

        ServicePortDescriptor<AdapterComponentXDRRequestPortType> portDescriptor = new AdapterComponentDocSubmissionRequestServicePortDescriptor();

        CONNECTClient<AdapterComponentXDRRequestPortType> client = getCONNECTClientUnsecured(portDescriptor, url,assertion);
        //client.enableMtom();
        try {
            XDRAcknowledgementType response = (XDRAcknowledgementType) client.invokePort(AdapterComponentXDRRequestPortType.class,"provideAndRegisterDocumentSetBRequest", request);
            if(response == null){
                System.out.println("Response Object is null");
                return sub;
            }
            System.out.println(response.getMessage().getStatus());
           // String str = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:RequestAccepted";
            List<StatusChange> tempstchngList = sub.getStatusChange();
            if(tempstchngList == null){
                tempstchngList = new ArrayList<>();
                sub.setStatusChange(tempstchngList);
            }
            if(response.getMessage().getStatus().equals("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:RequestAccepted")){
              retVal = 0;
              System.out.println("Adding StatChange Item");
                StatusChange statChng = new StatusChange();
                statChng.setResult("Success");
                statChng.setStatus(response.getMessage().getStatus());
                statChng.setSubmissionId(sub);
                statChng.setSplitInformation(Integer.toString(SplitNum));
                sub.getStatusChange().add(statChng);

                // datamanager.commit(statChng);


            }else {
                StatusChange statChng = new StatusChange();
                statChng.setResult("Failure");
                statChng.setStatus(response.getMessage().getStatus());
                statChng.setSubmissionId(sub);
                statChng.setSplitInformation(   Integer.toString(SplitNum));

                sub.getStatusChange().add(statChng);

                    RegistryResponseType rr =response.getMessage();
                     List<Error> temperrList = sub.getError();
                     if(temperrList == null){
                         temperrList = new ArrayList<>();
                         sub.setError(temperrList);
                     }
                     List<RegistryError>  errList = rr.getRegistryErrorList().getRegistryError();

            for (Iterator iterator = errList.iterator(); iterator.hasNext();) {
                RegistryError value = (RegistryError) iterator.next();
                Error tempErr = new Error();
                tempErr.setErrorCode(value.getErrorCode());
                tempErr.setError(value.getValue());
                tempErr.setCodeContext(value.getCodeContext());
                tempErr.setSeverity(value.getSeverity());
                tempErr.setSplitInformation(SplitNum);
                tempErr.setSubmissionId(sub);
                temperrList.add(tempErr);


            }
            sub.setError(temperrList);
            }







        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            Iterator it = filestoDel.iterator();
            while(it.hasNext()) {
                new File(it.next().toString()).delete();
            }
        }

      return sub;
    }

    protected SlotType1 createSlotIndividual(String name, String value) {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        return createSlot(name,values);
    }

    protected SlotType1 createSlot(String name, ArrayList<String> values) {
        SlotType1 slot = new SlotType1();
        slot.setName(name);
        ValueListType vls = new ValueListType();
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            String value = (String) iterator.next();
            vls.getValue().add(value);

        }
        slot.setValueList(vls);
        return slot;
    }
    protected InternationalStringType createInternationalStringType(String value){
        InternationalStringType  locVar = new InternationalStringType();
        LocalizedStringType tempLocStr = new LocalizedStringType();
        tempLocStr.setValue(value);
        locVar.getLocalizedString().add(tempLocStr);
        return locVar;
    }
    protected ExtrinsicObjectType  CreateExtrinicObject(Submission sub, Document dt,int idx){
        ExtrinsicObjectType tempExtObj = new ExtrinsicObjectType();
        String docid = "Document";
        if(idx<10)
            docid = docid+"0"+Integer.toString(idx);
        else
            docid = docid + Integer.toString(idx);

        tempExtObj.setId(docid);
        if(dt.getDocument_type() == DocumentType.PDF)
        tempExtObj.setMimeType(DOCUMENT_TYPE);
        else if(dt.getDocument_type() == DocumentType.XML)
        tempExtObj.setMimeType(DOCUMENT_TYPE_XML);

        tempExtObj.setObjectType(DOCUMENT_OBJECTTYPE);


        ClassificationType tempextClass = createClassificationType(DOC_CLASSIFICATION_ID1,DOC_CLASSIFICATION_ID_SCHEME1,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION1,docid);
        tempextClass.getSlot().add(createSlotIndividual("formatCode","1"));
        tempextClass.setName(createInternationalStringType(DOC_FORMAT_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass);
        ClassificationType tempextClass1 = createClassificationType(DOC_CLASSIFICATION_ID2,DOC_CLASSIFICATION_ID_SCHEME2,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION2,docid);
        tempextClass1.getSlot().add(createSlotIndividual("authorInstitution",sub.getAuthorNPI()));
        tempExtObj.getClassification().add(tempextClass1);
        ClassificationType tempextClass2 = createClassificationType(DOC_CLASSIFICATION_ID3,DOC_CLASSIFICATION_ID_SCHEME3,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION3,docid);
        tempextClass2.getSlot().add(createSlotIndividual("classCode","1"));
        tempextClass2.setName(createInternationalStringType(DOC_CLASS_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass2);
        ClassificationType tempextClass3 = createClassificationType(DOC_CLASSIFICATION_ID4,DOC_CLASSIFICATION_ID_SCHEME4,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION4,docid);
        tempextClass3.getSlot().add(createSlotIndividual("confidentialityCode","V"));
        tempextClass3.setName(createInternationalStringType(DOC_CONFIDENTIALITY_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass3);
        ClassificationType tempextClass4 = createClassificationType(DOC_CLASSIFICATION_ID5,DOC_CLASSIFICATION_ID_SCHEME5,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION5,docid);
        tempextClass4.getSlot().add(createSlotIndividual("practiceSettingCode","1"));
        tempextClass4.setName(createInternationalStringType(DOC_PRACSETTINGS_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass4);
        ClassificationType tempextClass5 = createClassificationType(DOC_CLASSIFICATION_ID6,DOC_CLASSIFICATION_ID_SCHEME6,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION6,docid);
        tempextClass5.getSlot().add(createSlotIndividual("healthcareFacilityTypeCode","1"));
        tempextClass5.setName(createInternationalStringType(DOC_HEALTHCAREFACILITY_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass5);
        ClassificationType tempextClass6 = createClassificationType(DOC_CLASSIFICATION_ID7,DOC_CLASSIFICATION_ID_SCHEME7,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION7,docid);
        tempextClass6.getSlot().add(createSlotIndividual("codingScheme","2"));
        tempextClass6.setName(createInternationalStringType(DOC_CODINGSCHEME_CODE_TYPE));
        tempExtObj.getClassification().add(tempextClass6);
        ClassificationType tempextClass7 = createClassificationType(DOC_CLASSIFICATION_ID8,DOC_CLASSIFICATION_ID_SCHEME8,DOC_CLASSIFICATION_ID_NODE_REPRESENTATION8,docid);
        tempextClass7.getSlot().add(createSlotIndividual("classCode","1"));
        tempextClass7.setName(createInternationalStringType(DOC_CLASSCODE_TYPE));
        tempExtObj.getClassification().add(tempextClass7);
        String dtcurrent = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss").format(Calendar.getInstance().getTime());

        tempExtObj.getSlot().add(createSlotIndividual("creationTime",dtcurrent));
        tempExtObj.getSlot().add(createSlotIndividual("hash",DOC_HASH_STRING));
        String langcode = "en-us";
        if(dt.getLanguage() != null)
            langcode = dt.getLanguage().getId();

        tempExtObj.getSlot().add(createSlotIndividual("languageCode",langcode));
        tempExtObj.getSlot().add(createSlotIndividual("legalAuthenticator","NA"));
        SlotType1 slottemp = new SlotType1();
        slottemp.setName("attachmentControlNumber");
        ValueListType tempsltype = new ValueListType();
        tempsltype.getValue();
        if((dt.getAttachmentControlNumber() != null)|| (dt.getAttachmentControlNumber() != "")){
            tempsltype.getValue().add(dt.getAttachmentControlNumber());
        }

        slottemp.setValueList(tempsltype);
        //slottemp.getValueList();
        tempExtObj.getSlot().add(slottemp);


        tempExtObj.getSlot().add(createSlotIndividual("serviceStartTime",dtcurrent));
        tempExtObj.getSlot().add(createSlotIndividual("serviceEndTime",dtcurrent));
        tempExtObj.getSlot().add(createSlotIndividual("size",dt.getFileDescriptor().getSize().toString()));
        tempExtObj.setName(createInternationalStringType(DOC_EXTROBJ_NAME));

        tempExtObj.setDescription(createInternationalStringType(sub.getPurposeOfSubmission().getPurposeOfSubmission()));
        return tempExtObj;
    }
   /* protected void ReplaceDocumentContentXML(String base64Str){
        try {
            Path path = Paths.get("C:/New folder/record.xml");
            Stream<String> lines = Files.lines(path);
            List <String> replaced = lines.map(line -> line.replaceAll("foo", "bar")).collect(Collectors.toList());
            Files.write(path, replaced);
            lines.close();
            System.out.println("Find and Replace done!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    protected ExternalIdentifierType createExternalIdentifier(String id, String value) {
        ExternalIdentifierType e = new ExternalIdentifierType();
        e.setId(id);
        LocalizedStringType l = new LocalizedStringType();
        l.setValue(value);
        e.getName().getLocalizedString().add(l);
        return e;
    }
    protected ClassificationType createClassificationType(String id, String classificationScheme,String nodeRepresentation,String classifiedObject) {
        ClassificationType e = new ClassificationType();
        e.setId(id);
        e.setClassificationScheme(classificationScheme);
        e.setNodeRepresentation(nodeRepresentation);
        e.setClassifiedObject(classifiedObject);
        return e;
    }
    protected CONNECTClient<AdapterComponentXDRRequestPortType> getCONNECTClientUnsecured(
            ServicePortDescriptor<AdapterComponentXDRRequestPortType> portDescriptor, String url,
            AssertionType assertion) {

        return CONNECTCXFClientFactory.getInstance().getCONNECTClientUnsecured(portDescriptor, url, assertion);
    }

    public Submission SubmiteMDRRequest(Providers prov, boolean bReg){
        Submission sub = prov.getSubmissionID();
        ServiceRegistrationRequest  regReq = new ServiceRegistrationRequest();

        if(bReg)
           regReq.setActionRequested(ActionType.A);
        else
            regReq.setActionRequested(ActionType.R);

        ProviderInfoList provList = new ProviderInfoList();
        ProviderInfoType provType = new ProviderInfoType();
        provType.setProviderName(prov.getName());
        provType.setProviderNPI(prov.getProvider_npi());
        provType.setProviderNumber("AC342342");
        provType.setProviderTaxID("213198102");
        AddrInfoType addrInfoType = new AddrInfoType();
        addrInfoType.setAddrLine1(prov.getAddrLine1());
        addrInfoType.setAddrLine2(prov.getAddrLine2());
        addrInfoType.setCity(prov.getCity());
        addrInfoType.setState(prov.getState());
        addrInfoType.setZipCode(prov.getZipcode());
        provType.setAddrInfo(addrInfoType);
        ServiceInfoList serviceInfoList = new ServiceInfoList();
        ServiceInfoType serviceInfoType = new ServiceInfoType();
        serviceInfoType.setServiceCode("EMDR");
        XMLGregorianCalendar calendar=null, endcalendar=null;

        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            calendar=df.newXMLGregorianCalendar("2019-04-01");
            endcalendar = df.newXMLGregorianCalendar("2020-03-31");

        }
        catch (  DatatypeConfigurationException e) {

        }





        JAXBElement<XMLGregorianCalendar> element = new JAXBElement<XMLGregorianCalendar>(new QName("startDate"),XMLGregorianCalendar.class, null);
        element.setValue(calendar);

        serviceInfoType.setStartDate(element);

        JAXBElement<XMLGregorianCalendar> endelement = new JAXBElement<XMLGregorianCalendar>(new QName( "endDate"),XMLGregorianCalendar.class, endcalendar);

        serviceInfoType.setEndDate(endelement);
        serviceInfoList.getServiceInfo().add(serviceInfoType);
        provType.setServiceInfoList(serviceInfoList);
        provList.getProviderInfo().add(provType);

        regReq.setProviderInfoList(provList);

        try
        {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceRegistrationRequest.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(regReq, sw);
            String xmlContent = sw.toString();
            //String toReplace = "xmlns=\"\" xmlns:ns2=\"http://www.cms.gov/esMD/schemas/v2/serviceregistration\"";
            xmlContent = xmlContent.replace(" xmlns=\"\" xmlns:ns2=\"http://www.cms.gov/esMD/schemas/v2/serviceregistration\"","");
            System.out.println( xmlContent );
            //Verify XML Content

            System.out.println("xml content length is "+xmlContent.length());
            Document doc = new Document();
            doc.setSubmissionID(prov.getSubmissionID());
            FileDescriptor fd = datamanager.create(FileDescriptor.class);
            fd.setName(UUID.randomUUID().toString()+".xml");
            fd.setExtension("xml");
            fd.setCreateDate(new Date());


                //byte[] bytes = locFile.getBytes();
                fd.setSize((long) xmlContent.length());
                InputStream targetStream = new ByteArrayInputStream(xmlContent.getBytes());

            fileLoader.saveStream(fd, () -> targetStream);

            datamanager.commit(fd);
            doc.setFileDescriptor(fd);
            doc.setDocument_type(DocumentType.XML);
            datamanager.commit(doc);
            ArrayList<Document> docArryList = new ArrayList<>();
            docArryList.add(doc);
            prov.getSubmissionID().setDocument(docArryList);
            sub = SoapClientCall(prov.getSubmissionID(),"1-1",null,docArryList);

            datamanager.remove(fd);
            doc.setFileDescriptor(null);
            datamanager.remove(doc);
            //fileLoader.
            fileLoader.removeFile(fd);

            prov.getSubmissionID().setDocument(null);

            fd = null;
            //datamanager.commit(prov.getSubmissionID());
        } catch (Exception e) {
           System.out.println("Exception Occured");
        }


        return sub;
    }
}