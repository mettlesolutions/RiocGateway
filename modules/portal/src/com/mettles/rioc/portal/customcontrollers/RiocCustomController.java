package com.mettles.rioc.portal.customcontrollers;

import com.mettles.rioc.*;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.entity.Error;
import com.mettles.rioc.portal.jsonobjects.*;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.portal.App;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.bind.annotation.*;
import com.mettles.rioc.service.SubmissionService;
import org.springframework.web.multipart.MultipartFile;


import javax.inject.Inject;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping("/riocapi")
public class RiocCustomController {

    @Inject
    private SubmissionService someService;
    @Inject
    private DataManager datamanager;

    @Inject
    private FileLoader fileLoader;

    private String EDI;

    @PostMapping("/provider")
    public @ResponseBody
    ProviderResponseModel createProvider(@RequestBody ProviderRequestModel lb) {
        ProviderResponseModel retVal = new ProviderResponseModel();
        if(lb.getProvider_name() == null || lb.getProvider_name().equals("")){
            retVal.setCall_error_code("1");
            retVal.setCall_error_description("Provider name is mandatory");
            return retVal;
        }
        if(lb.getProvider_npi() == null || lb.getProvider_npi().equals("")){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("Provider NPI is mandatory");
            return retVal;
        }

        if(lb.getProvider_street() == null || lb.getProvider_street().equals("")){
            retVal.setCall_error_code("3");
            retVal.setCall_error_description("Provider Street Address is mandatory");
            return retVal;
        }

        if(lb.getProvider_city() == null || lb.getProvider_city().equals("")){
            retVal.setCall_error_code("4");
            retVal.setCall_error_description("Provider City is mandatory");
            return retVal;
        }

        if(lb.getProvider_state() == null || lb.getProvider_state().equals("")){
            retVal.setCall_error_code("5");
            retVal.setCall_error_description("Provider State is mandatory");
            return retVal;
        }

        if(lb.getProvider_zip() == null || lb.getProvider_zip().equals("")){
            retVal.setCall_error_code("7");
            retVal.setCall_error_description("Provider zip is mandatory");
            return retVal;
        }
        Providers prov = datamanager.create(Providers.class);
        prov.setName(lb.getProvider_name());
        prov.setProvider_npi(Long.parseLong(lb.getProvider_npi()));
        prov.setAddrLine1(lb.getProvider_street());
        if(lb.getProvider_street2() != null)
            prov.setAddrLine2(lb.getProvider_street2());
        prov.setCity(lb.getProvider_city());
        prov.setState(lb.getProvider_state());
        prov.setZipcode(lb.getProvider_zip());
        datamanager.commit(prov);
        retVal.setProvider_id(String.valueOf(prov.getId()));
        return retVal;
    }

    @RequestMapping(value="/provider/{id}", method= POST)
    public @ResponseBody
    ProviderResponseModel PostProviderReg(@PathVariable Long id,@RequestBody PostProviderRequestModel tempReqObj){
        ProviderResponseModel retval = new ProviderResponseModel();
        Providers prov = null;
        try {
             prov = datamanager.load(Providers.class).id(id).view("provider-view").one();
          }catch(Exception e)
          {
              retval.setCall_error_code("2");
              retval.setCall_error_description("No records found for the Provider ID ");
              return retval;
           }
        if(prov == null){
            retval.setCall_error_code("2");
            retval.setCall_error_description("No records found for the Provider ID ");
            return retval;
        }
        Submission sub =   prov.getSubmissionID();
        if(sub == null) {
         /*   if (tempReqObj.isbRegister()) {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
            } else {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
            }*/

            sub = datamanager.create(Submission.class);
            sub.setAuthorNPI("1234567890");
            sub.setTitle("Emdr Registration");
            String Recepient_oid = "2.16.840.1.113883.3.6037.2.47";
            Recepient intdRecp = datamanager.loadValue(
                    "select o from rioc_Recepient o " +
                            "where o.oid = :recep ", Recepient.class)
                    .parameter("recep", Recepient_oid)
                    .one();
            if (intdRecp == null) {
                //Show error
                retval.setCall_error_code("5");
                retval.setCall_error_description("Intended Recepient Not Found");
                return retval;
               // return;
            }
            LineofBusiness purpOfSub = null;
            String contentType = "5";
            purpOfSub = datamanager.loadValue(
                    "select o from rioc_LineofBusiness o " +
                            "where o.contentType = :purp ", LineofBusiness.class)
                    .parameter("purp", contentType)
                    .one();

            if (purpOfSub == null) {
                //Show error
                retval.setCall_error_code("3");
                retval.setCall_error_description("Purpose of Submission Not Found");
                return retval;
            }

            sub.setIntendedRecepient(intdRecp);
            sub.setPurposeOfSubmission(purpOfSub);

            sub.setLastSubmittedSplit(1);
            sub.setHighestSpiltNo(1);
            prov.setSubmissionID(sub);
            datamanager.commit(sub);
        }else{
            sub = prov.getSubmissionID();
        }
        boolean bRegister = false;
        if(tempReqObj.isRegister_with_emdr())
        System.out.println("Boolean bregister true" );
        else
            System.out.println("Boolean bregister false" );


        if(sub.getStage().equals("Draft")){
            if (tempReqObj.isRegister_with_emdr() ) {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                bRegister = true;
            } else {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                bRegister = false;
            }
        }else if(sub.getStage().equals("esMD - Request Accepted")){
            if(prov.getRegistered_for_emdr()){

                if (tempReqObj.isRegister_with_emdr()) {
                    retval.setCall_error_code("4");
                    retval.setCall_error_description("Already Registered");
                    return retval;
                }else{
                    bRegister = false;
                    prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                }

            }else {

                if (!tempReqObj.isRegister_with_emdr()) {
                    retval.setCall_error_code("4");
                    retval.setCall_error_description("Already DeRegistered");
                    return retval;
                }else{
                    bRegister = true;
                    prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                }

            }

        }else{
            if (tempReqObj.isRegister_with_emdr()) {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                bRegister = true;
            } else {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                bRegister = false;
            }

        }
        prov.getSubmissionID().setStage("Draft");
        Submission temp = AppBeans.get(SubmissionService.class).SubmiteMDRRegFile(prov, bRegister);
       // prov.getSubmissionID().setUniqueIdList(temp.getUniqueIdList());
       // datamanager.commit(prov.getSubmissionID());
        datamanager.commit(prov);

        if(temp.getError() != null){
            if(temp.getError().size() > 0) {
                System.out.println("Error occured while submitting");
            }

        }else{
            if (tempReqObj.isRegister_with_emdr())
                prov.setRegistered_for_emdr(true);
            else
                prov.setRegistered_for_emdr(false);
        }
        //  dataManager.commit(currObj);
        CommitContext commitContext = new CommitContext();


        if (temp.getStatusChange().size() > 0) {

            System.out.println("Adding Status change items");
            Iterator<StatusChange> statchngit = temp.getStatusChange().iterator();
            while (statchngit.hasNext()) {
                StatusChange stat = statchngit.next();
                if(stat.getCreateTs() != null)
                    continue;

                StatusChange stchng = datamanager.create(StatusChange.class);
                stchng.setSubmissionId(sub);
                stchng.setStatus(stat.getStatus());
                if(stat.getResult().equals("Success")){
                    retval.setProvider_status("Submitted");
                }else{
                    retval.setProvider_status("Failure");
                }
                stchng.setResult(stat.getResult());
                stchng.setSplitInformation(stat.getSplitInformation());
                System.out.println(stat.getStatus());
                System.out.println(stat.getResult());
                commitContext.addInstanceToCommit(stchng);



            }
           // sub.setStatusChange(statusChangeDc.getItems());

        } else {
            System.out.println("Error Occured While Submitting");
        }
        if(temp.getError() != null) {
            if (temp.getError().size() > 0) {
                Iterator<Error> errIt = temp.getError().iterator();
                while (errIt.hasNext()) {
                    Error staterr = errIt.next();
                    if(staterr.getCreateTs() != null)
                        continue;

                    Error errTemp = datamanager.create(Error.class);
                    errTemp.setSubmissionId(sub);
                    errTemp.setSeverity(staterr.getSeverity());
                    errTemp.setCodeContext(staterr.getCodeContext());
                    errTemp.setErrorCode(staterr.getErrorCode());
                    errTemp.setError(staterr.getError());

                    commitContext.addInstanceToCommit(errTemp);


                }

            } else {
                System.out.println("Error Table is Empty");
            }
        }
        //  commitContext.addInstanceToCommit(submissionDc.getItem());
        //  providersDc.getItem().setSubmissionID(currObj);
        //  commitContext.addInstanceToCommit(providersDc.getItem());
        datamanager.commit(commitContext);

        LoadContext<Submission> loadContext = LoadContext.create(Submission.class)
                .setId(sub.getId()).setView("submission-view");
        Submission subTemp =  datamanager.load(loadContext);
        subTemp.setUniqueIdList(temp.getUniqueIdList());
        datamanager.commit(subTemp);

        retval.setProvider_id(String.valueOf(prov.getId()));
        return retval;
    }
    @RequestMapping(value="/pdfreadwrite/{id}", method= GET)
    public @ResponseBody
    ProviderRegStatusResponseModel PDFReadWriter(@PathVariable Long id) {
        ProviderRegStatusResponseModel retVal = new ProviderRegStatusResponseModel();
        AppBeans.get(SubmissionService.class).PDFReadWrite();

        return retVal;
    }

    @RequestMapping(value="/provider/{id}", method= GET)
    public @ResponseBody
    ProviderRegStatusResponseModel GetProviderRegStatus(@PathVariable Long id){
        ProviderRegStatusResponseModel retVal = new ProviderRegStatusResponseModel();
        retVal.setProvider_id(id.toString());
        Providers prov = null;
        try {
             prov = datamanager.load(Providers.class).id(id).view("provider-view").one();
        }catch(Exception e){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the Provider Id provided");
            return retVal;
        }
        if(prov == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the Provider Id provided");
            return retVal;
        }
        retVal.setProvider_name(prov.getName());
        retVal.setProvider_street(prov.getAddrLine1());
        if(prov.getAddrLine2() != null)
            retVal.setProvider_street2(prov.getAddrLine2());

        retVal.setProvider_city(prov.getCity());
        retVal.setProvider_state(prov.getState());
        retVal.setProvider_zip(prov.getZipcode());

        if(prov.getSubmissionID() == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("Registration not submitted");
            retVal.setSubmission_status("Not Submitted");
            return retVal;
        }else{
            retVal.setSubmission_status("Submitted");
        }

        retVal.setStage(prov.getSubmissionID().getStage());
        retVal.setStatus(prov.getSubmissionID().getStatus());
        retVal.setTransaction_id_list(prov.getSubmissionID().getTransactionIdList());
        retVal.setReg_status(prov.getLast_submitted_transaction().getId());
        Iterator<Error> errorIterator ;
        if(prov.getSubmissionID().getError() != null) {
            errorIterator = prov.getSubmissionID().getError().iterator();
            ArrayList<ErrorJsonObject> errorJsonObjectArrayList = new ArrayList<>();
            while(errorIterator.hasNext()){
                Error errTemp = errorIterator.next();
                ErrorJsonObject errRetVal = new ErrorJsonObject();
                errRetVal.setError_code(errTemp.getErrorCode());
                errRetVal.setError_context(errTemp.getCodeContext());
                errRetVal.setEsmd_transaction_id(errTemp.getEsmdTransactionId());
                errRetVal.setName(errTemp.getError());
                errRetVal.setSeverity(errTemp.getSeverity());
                errRetVal.setSplit_number(errTemp.getSplitInformation());
                errorJsonObjectArrayList.add(errRetVal);
            }
            retVal.setErrors(errorJsonObjectArrayList);
        }
        Iterator<StatusChange> statusChangeIterator ;
        if(prov.getSubmissionID().getStatusChange() != null) {
            statusChangeIterator = prov.getSubmissionID().getStatusChange().iterator();
            ArrayList<StatusChangesJsonObject> statusChangesJsonObjectArrayList = new ArrayList<>();
            while(statusChangeIterator.hasNext()){
                StatusChange stchngTemp = statusChangeIterator.next();
                StatusChangesJsonObject statchngRetVal = new StatusChangesJsonObject();
                statchngRetVal.setEsmd_transaction_id(stchngTemp.getEsmdTransactionId());
                statchngRetVal.setStatus(stchngTemp.getStatus());
                statchngRetVal.setSplit_number(stchngTemp.getSplitInformation());
                statchngRetVal.setTitle(stchngTemp.getResult());
                DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS:ss");
                String strDate = dateFormat.format(stchngTemp.getCreateTs());
                statchngRetVal.setTime(strDate);
                statusChangesJsonObjectArrayList.add(statchngRetVal);
            }
            retVal.setStatus_changes(statusChangesJsonObjectArrayList);
        }
        return retVal;
    }

    SubmissionResponseModel validateX12SubInputs( SubmissionRequestModel lb,SubmissionResponseModel ret,LineofBusiness purpOfSub,Recepient intdRecp){

        if(!purpOfSub.getIsX12Supported()){
            ret.setCall_error_code("5");
            ret.setCall_error_description("Purpose of Submission doesnt support X12");
            return ret;
        }

        if(lb.isAuto_split()){

            ret.setCall_error_code("7");
            ret.setCall_error_description("Split is not supported for X12 ");
            return ret;

        }

        if(intdRecp.getEdiID() == null){

            ret.setCall_error_code("9");
            ret.setCall_error_description("No EDI Configured for this RC cannot send X12 ");
            return ret;
        }

        if(StringUtils.isEmpty(lb.getEsMD_claim_id())){
            ret.setCall_error_code("10");
            ret.setCall_error_description("Claimid cannot be empty for PWK X12 submission");
            return ret;
        }

        {
            String attachmentControlNum = null;
            int splitNum = 0;
            Iterator<DocumentSetJsonObject> dociterator = lb.getDocument_set().iterator();
            while(dociterator.hasNext()){
                DocumentSetJsonObject dtTemp = dociterator.next();
                 if(dtTemp.getSplit_no() != null){
                     if(splitNum == 0){
                         splitNum = dtTemp.getSplit_no();
                     }else if(dtTemp.getSplit_no() != splitNum){
                         ret.setCall_error_code("12");
                         ret.setCall_error_description("Splits are not supported for PWK");
                         return ret;
                     }
                 }

                if(dtTemp.getAttachmentControlNum()  ==  null || dtTemp.getAttachmentControlNum().equals("")){
                    ret.setCall_error_code("11");
                    ret.setCall_error_description("Attachment Control Number connot be null for PWK");
                    return ret;
                }else{
                    if(attachmentControlNum  == null){
                        attachmentControlNum = dtTemp.getAttachmentControlNum();
                    }else{
                        if(!attachmentControlNum.equals(dtTemp.getAttachmentControlNum())){
                            ret.setCall_error_code("11");
                            ret.setCall_error_description("Attachment Control Number for all the documents needs to be same");
                            return ret;
                        }
                    }
                }
            }
        }
        return null;
    }

    @PostMapping("/submission")
    public @ResponseBody
    SubmissionResponseModel createSubmission(@RequestBody SubmissionRequestModel lb) {

        SubmissionResponseModel temp = new SubmissionResponseModel();
        if(lb.getName() == null || lb.getName().equals("")){
            temp.setCall_error_code("6");
            temp.setCall_error_description("Title is mandatory");
            return temp;
        }
        if(lb.getAuthor_npi() == null || lb.getAuthor_npi().equals("")){
            temp.setCall_error_code("6");
            temp.setCall_error_description("NPI of the provider is mandatory");
            return temp;
        }
        if(lb.getIntended_recepient() == null || lb.getIntended_recepient().equals("")){
            temp.setCall_error_code("8");
            temp.setCall_error_description("Recepient should be provided");
            return temp;
        }
        if(lb.getPurpose_of_submission() == null || lb.getPurpose_of_submission().equals("")){
            temp.setCall_error_code("2");
            temp.setCall_error_description("Purpose of submission should be provided");
            return temp;
        }
        if(lb.getDocument_set() == null || lb.getDocument_set().size() == 0){
            temp.setCall_error_code("10");
            temp.setCall_error_description("At least one document should be provided");
            return temp;
        }
        Recepient intdRecp = datamanager.loadValue(
                "select o from rioc_Recepient o " +
                        "where o.oid = :recep ", Recepient.class)
                .parameter("recep", lb.getIntended_recepient())
                .one();
        if(intdRecp == null){
            temp.setCall_error_code("5");
            temp.setCall_error_description("Recepient not found");
            return temp;
        }
        LineofBusiness purpOfSub = null;
        purpOfSub =   datamanager.loadValue(
                "select o from rioc_LineofBusiness o " +
                        "where o.contentType = :purp ", LineofBusiness.class)
                .parameter("purp", lb.getPurpose_of_submission())
                .one();

        if(purpOfSub == null){
            temp.setCall_error_code("3");
            temp.setCall_error_description("Purpose of submission not found");
            return temp;
        }

        if(lb.isbSendinX12()){
            SubmissionResponseModel retval =  validateX12SubInputs(lb,temp,purpOfSub,intdRecp);
            if(retval != null)
                return retval;
        }
       // boolean esmdClai
        if((purpOfSub.getIsEsmdClaimIdMandatory() != null)){
            if(purpOfSub.getIsEsmdClaimIdMandatory() ) {
                if (lb.getEsMD_claim_id() == null || lb.getEsMD_claim_id().equals("")) {
                    temp.setCall_error_code("4");
                    temp.setCall_error_description("Esmd Claim Id mandatory for this line of business");
                    return temp;
                }
            }
        }

       if(lb.isAuto_split() && lb.getDocument_set().size() > 1){

           temp.setCall_error_code("2");
           temp.setCall_error_description("AutoSplit is enabled please upload only one Document");
           return temp;
       }
        Submission subTemp = datamanager.create(Submission.class);

        subTemp.setAuthorNPI(lb.getAuthor_npi());
        subTemp.setTitle(lb.getName());
        subTemp.setComments(lb.getComments());

        if(lb.getEsMD_claim_id()!= null) {
        if(lb.getEsMD_claim_id().length() > 0 & purpOfSub.getIsEsmdClaimDisplayed()) {
            subTemp.setEsMDClaimID(lb.getEsMD_claim_id());
        }
        }
        if(lb.getEsmd_case_id() != null) {
        if(lb.getEsmd_case_id().length() > 0 &  purpOfSub.getIsCaseIdDisplayed()){
                subTemp.setEsmdCaseId(lb.getEsmd_case_id());
            }
        }
        if(lb.getComments() != null){
        if(lb.getComments().length() > 0) {
            subTemp.setComments(lb.getComments());
        }
        }
        subTemp.setPurposeOfSubmission(purpOfSub);
        subTemp.setIntendedRecepient(intdRecp);

        //int retVal = AppBeans.get(SubmissionService.class).CallConnectApi(subTemp,subTemp.getDocument().iterator().next());
        subTemp.setAutoSplit(lb.isAuto_split());
        if(lb.isbSendinX12())
        subTemp.setBSendinX12(lb.isbSendinX12());

        datamanager.commit(subTemp);
        List<Document> docSubSet = new ArrayList<>();
        Iterator<DocumentSetJsonObject> dociterator = lb.getDocument_set().iterator();
        while(dociterator.hasNext()) {
            Document docTemp = datamanager.create(Document.class);
            DocumentSetJsonObject dtJsonTemp = dociterator.next();
            docTemp.setFilename(dtJsonTemp.getFilename());
            docTemp.setTitle(dtJsonTemp.getName());
            docTemp.setSubmissionID(subTemp);
            docTemp.setSplitNumber(dtJsonTemp.getSplit_no());
            if(dtJsonTemp.getAttachmentControlNum() != null)
                docTemp.setAttachmentControlNumber(dtJsonTemp.getAttachmentControlNum());




            datamanager.commit(docTemp);
            docSubSet.add(docTemp);
        }
        subTemp.setDocument(docSubSet);

        temp.setSubmission_id(subTemp.getId().toString());
        return temp;

    }

    public void submitX12275Document(Submission sub){

        X12275SubmissionStatus status = AppBeans.get(SubmissionService.class).SubmitX12275Submission(sub);
        CommitContext commitContext = new CommitContext();

        if(status.getErrorCode().equals("Success")){
            sub.setStatus("Success");
            sub.setStage("Submitted");
            sub.setUniqueIdList(status.getUniqueID());
        }else{
            sub.setStatus(status.getErrorCode());
            sub.setStage(status.getErrorMessage());
            sub.setUniqueIdList(status.getUniqueID());
        }
        {
            System.out.println("Entered into get status change");
            Iterator<Document> documentIterator = sub.getDocument().iterator();
            while(documentIterator.hasNext()){
                Document dtTemptoRem = documentIterator.next();

                datamanager.remove(dtTemptoRem.getFileDescriptor());
                try {
                    // dtTemptoRem.getFileDescriptor().
                    // fileLoader.
                    fileLoader.removeFile(dtTemptoRem.getFileDescriptor());
                }catch(Exception e){
                    System.out.println("Exception Occured while removing the file");
                }

                dtTemptoRem.setFileDescriptor(null);
                // commitContext.addInstanceToCommit(dtTemptoRem);

            }
        }
        commitContext.addInstanceToCommit(sub);
        datamanager.commit(commitContext);
    }

    @RequestMapping(value="/submission/{id}", method= POST)
    public @ResponseBody
    SubmissionResponseModel PostSubmission(@PathVariable Long id,@ModelAttribute PostSubmissionRequest tempReqObj){
        SubmissionResponseModel retVal = new SubmissionResponseModel();
        List<MultipartFile> localFiles = tempReqObj.getUploadFiles();
        Submission sub = null;
        try {
             sub = datamanager.load(Submission.class).id(id).view("submission-view").one();

        }catch(Exception e){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        if(sub == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        List<Document> docObj = sub.getDocument();
        if(!sub.getStage().equals("Draft")){
            retVal.setCall_error_code("3");
            retVal.setCall_error_description("Post call can be used only during draft stage");
            return retVal;
        }
        if(localFiles==null || (localFiles.size() ==0)){
            retVal.setCall_error_code("5");
            retVal.setCall_error_description("You should submit at least one file");
            return retVal;
        }
        if((sub.getAutoSplit()) && (localFiles.size() > 1)){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("AutoSplit is enabled please upload only one Document");
            return retVal;

        }
        if(docObj.size() != localFiles.size())
        {
            retVal.setCall_error_code("11");
            retVal.setCall_error_description("Incorrect number of files sent");
            return retVal;
        }
        System.out.println(localFiles.size());
        Iterator<MultipartFile> filesIt = localFiles.iterator();
        HashMap<String,MultipartFile> dupFileUpload = new HashMap<>();
        HashMap<String,Document> dupDocUpload = new HashMap<>();
        while(filesIt.hasNext()){

            Iterator<Document> docsetItr = docObj.iterator();
            MultipartFile tmpFile = filesIt.next();
            while(docsetItr.hasNext()){
                Document tempDt = docsetItr.next();

                if(tempDt.getFilename().equals(tmpFile.getOriginalFilename())){

                    if(!dupFileUpload.containsKey(tempDt.getId().toString())) {
                        dupFileUpload.put(tempDt.getId().toString(), tmpFile);
                        dupDocUpload.put(tempDt.getId().toString(), tempDt);
                        break;
                    }

                }
            }
        }
        if(dupFileUpload.size() != localFiles.size())
        {
            retVal.setCall_error_code("4");
            retVal.setCall_error_description("File not found in the document list");
            return retVal;
        }

        for (Map.Entry me : dupFileUpload.entrySet()) {
            MultipartFile locFile = (MultipartFile)me.getValue();

            Document locDt = (Document)dupDocUpload.get(me.getKey());
            System.out.println("Split number is "+locDt.getSplitNumber());
            FileDescriptor fileDescriptor = datamanager.create(FileDescriptor.class);
            fileDescriptor.setName(locFile.getOriginalFilename());
            fileDescriptor.setExtension("pdf"); // As we will be supporting only pdf
            fileDescriptor.setCreateDate(new Date());
            try {

                //byte[] bytes = locFile.getBytes();
                fileDescriptor.setSize((long) locFile.getSize());
                InputStream is = locFile.getInputStream();
                fileLoader.saveStream(fileDescriptor, () -> is);
            } catch (FileStorageException | IOException e) {
                System.out.println("error occured");
                e.printStackTrace();
            }
            if((sub.getAutoSplit()) && (fileDescriptor.getSize() > 75000000)){
                datamanager.commit(fileDescriptor);
                locDt.setFileDescriptor(fileDescriptor);

                List<Document> docArryList = AppBeans.get(SubmissionService.class).SplitDocuments(locDt);
                if(docArryList.size() > 1){
                    datamanager.remove(fileDescriptor);
                    try {
                        fileLoader.removeFile(fileDescriptor);
                    }catch(Exception e){
                        System.out.println("Exception Occured while removing the file");
                    }
                    datamanager.remove(locDt);

                    Iterator<Document> doclistit = docArryList.iterator();
                    CommitContext docCommitCntx = new CommitContext();
                    List<Document> docList =new ArrayList<>();
                    while (doclistit.hasNext()) {
                        Document docRet = doclistit.next();
                        Document docCurr = null;
                        docCurr = datamanager.create(Document.class);

                        docCurr.setFileDescriptor(docRet.getFileDescriptor());
                        System.out.println("After Split file size is" + docRet.getFileDescriptor().getSize());
                        docCurr.setComments(docRet.getComments());
                        docCurr.setSplitNumber(docRet.getSplitNumber());
                        docCurr.setTitle(docRet.getTitle());
                        docCurr.setFilename(docRet.getFilename());
                        docCurr.setSubmissionID(sub);
                        docCurr.setAttachmentControlNumber(docRet.getAttachmentControlNumber());
                        docCurr.setLanguage(docRet.getLanguage());
                        docList.add(docCurr);
                       docCommitCntx.addInstanceToCommit(docCurr);

                    }
                    datamanager.commit(docCommitCntx);
                    sub.setDocument(docList);
                }

            }else {
                datamanager.commit(fileDescriptor);
                locDt.setFileDescriptor(fileDescriptor);
                datamanager.commit(locDt);
            }
        }
        if(sub.getBSendinX12()){
            submitX12275Document(sub);
            retVal.setSubmission_status(sub.getStage());
            retVal.setCall_error_code(sub.getStatus());
            retVal.setSubmission_id(id.toString());
            return retVal;

        }

        List<StatusChange> statchngList = new ArrayList<>();
        sub.setStatusChange(statchngList);
        List<Error> errchngList = new ArrayList<>();
        sub.setError(errchngList);
        if(sub.getDocument() == null)
            System.out.println("Document list is null");
        sub.getDocument().sort(Comparator.comparing(Document::getSplitNumber));
        int min_split =1,highest_split = 1;
        Iterator<Document> documentIt =sub.getDocument().iterator();
        Dictionary<Integer,ArrayList<Document>> splitDocDict = new Hashtable<>();
        Long threshold = 75000000L;
        Dictionary<Integer,Long> splitSize = new Hashtable<>();
        List <HIHConfiguration> hihConfigList = datamanager.load(HIHConfiguration.class)
                .query("select p from rioc_HIHConfiguration p")
                .view("HIHConfig-view")
                .list();
        if(hihConfigList == null){
            System.out.println("HIHConfiguration parameters are not set");

        }else if(hihConfigList.size() >0){
            Iterator<HIHConfiguration> hihConfigurationIterator = hihConfigList.iterator();
            if(hihConfigurationIterator.hasNext()){
                HIHConfiguration hihConfigParam = hihConfigurationIterator.next();
                threshold = Long.valueOf(hihConfigParam.getPayloadThreshold().longValue());
                threshold = threshold * 1000000L;
            }

        }

        while(documentIt.hasNext()){
            Document dtTemp = documentIt.next();

            int currSplitNum = dtTemp.getSplitNumber();
            if(((Hashtable<Integer, ArrayList<Document>>) splitDocDict).containsKey(currSplitNum)){
                ArrayList<Document> docArryList = splitDocDict.get(currSplitNum);
                Long currSize = splitSize.get(currSplitNum);
                currSize = currSize + dtTemp.getFileDescriptor().getSize();
                if(currSize > threshold){
                    System.out.println("Split size is greater than threshold");
                    retVal.setCall_error_code("6");
                    retVal.setCall_error_description("Split size is greater than threshold");
                    return retVal;
                }
                splitSize.put(currSplitNum,currSize);
                docArryList.add(dtTemp);
                splitDocDict.put(currSplitNum,docArryList);
            }else{
                Long size;
                ArrayList<Document> docArryList = new ArrayList<>();
                size = dtTemp.getFileDescriptor().getSize();
                if(size > threshold){
                    System.out.println("Split size is greater than threshold");
                    retVal.setCall_error_code("6");
                    retVal.setCall_error_description("Split size is greater than threshold");
                    return retVal;

                }
                splitSize.put(currSplitNum,size);
                docArryList.add(dtTemp);
                splitDocDict.put(currSplitNum,docArryList);
            }

            if(min_split > currSplitNum)
                min_split = currSplitNum;

            if(highest_split < currSplitNum)
                highest_split = currSplitNum;
        }
        if(min_split == highest_split){
            System.out.println("All splits are same");

        }
        int numofSplits = ((Hashtable<Integer, ArrayList<Document>>) splitDocDict).keySet().size();
        if((min_split != 1) || (highest_split != numofSplits)){
            System.out.println("Splits entered are incorrect please correct them");
            retVal.setCall_error_code("7");
            retVal.setCall_error_description("Splits entered are incorrect please correct them by passing consecutive integers staring at 1");
            return retVal;

        }
        String Splitstr = "1-"+numofSplits;
        Submission subretVal = null;
        subretVal =  someService.CallConnectApi(sub, Splitstr,null,splitDocDict.get(min_split));

        //subretVal.setUniqueIdList(subretVal.getUniqueIdList());
        subretVal.setLastSubmittedSplit(min_split);
        subretVal.setHighestSpiltNo(highest_split);
        StatusChange temp = null ;
        Iterator<StatusChange> statchngit = subretVal.getStatusChange().iterator();
        while( statchngit.hasNext()){
            temp = statchngit.next();
            datamanager.commit(temp);
        }

        subretVal.setStage("Submitted");
        if(temp.getResult().equals("Success")){
            subretVal.setStatus("Success");
            retVal.setSubmission_status("Submitted");
        }else{
            subretVal.setStatus("Failure");
            retVal.setSubmission_status("Failure");
            retVal.setCall_error_description(sub.getError().get(0).getDescription());
            retVal.setCall_error_code(sub.getError().get(0).getErrorCode());
            Iterator<Error> errListCommit = sub.getError().iterator();
            while(errListCommit.hasNext()){
              Error errtoCmmt = errListCommit.next();
                datamanager.commit(errtoCmmt);
            }
        }
        datamanager.commit(subretVal);
        if((subretVal.getStatusChange().size() != 0)||(subretVal.getError().size() != 0)){
            List<Document> dtArryList = sub.getDocument();
            Iterator<Document> dtIterator = dtArryList.iterator();
            List<Document> dtArryListUpdated = new ArrayList<>();
            while(dtIterator.hasNext()){
                Document dtTemp = dtIterator.next();
                if(dtTemp.getSplitNumber() == min_split) {
                    datamanager.remove(dtTemp.getFileDescriptor());
                    try {
                        fileLoader.removeFile(dtTemp.getFileDescriptor());
                    }catch(Exception e){
                        System.out.println("Exception Occured while removing the file");
                    }
                    // dtRemoveCtx.addInstanceToRemove(dtTemp.getFileDescriptor());
                    dtTemp.setFileDescriptor(null);
                  //   datamanager.commit(dtTemp);
                    //dtRemoveCtx.addInstanceToCommit(dtTemp);

                }
               // dtArryListUpdated.add(dtTemp);
            }
            // subretVal.setDocument(dtArryListUpdated);
            //datamanager.commit(dtRemoveCtx);


        }

        retVal.setSubmission_id(id.toString());

        return retVal;
    }

    @RequestMapping(value="/submission/{id}", method= GET)
    public @ResponseBody
    SubmissionGetStatusResponseModel PostSubmission(@PathVariable Long id){
        SubmissionGetStatusResponseModel retVal = new SubmissionGetStatusResponseModel();

        retVal.setSubmission_id(id.toString());
        Submission sub = null;
        try {
             sub = datamanager.load(Submission.class).id(id).view("submission-view").one();
        }catch(Exception e){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        if(sub == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        retVal.setStage(sub.getStage());
        retVal.setStatus(sub.getStatus());
        Iterator<Error> errorIterator ;
        if(sub.getError() != null) {
            errorIterator = sub.getError().iterator();
            ArrayList<ErrorJsonObject> errorJsonObjectArrayList = new ArrayList<>();
            while(errorIterator.hasNext()){
                Error errTemp = errorIterator.next();
                ErrorJsonObject errRetVal = new ErrorJsonObject();
                errRetVal.setError_code(errTemp.getErrorCode());
                errRetVal.setError_context(errTemp.getCodeContext());
                errRetVal.setEsmd_transaction_id(errTemp.getEsmdTransactionId());
                errRetVal.setName(errTemp.getError());
                errRetVal.setSeverity(errTemp.getSeverity());
                errRetVal.setSplit_number(errTemp.getSplitInformation());
                errorJsonObjectArrayList.add(errRetVal);
            }
            retVal.setErrors(errorJsonObjectArrayList);
        }
        Iterator<StatusChange> statusChangeIterator ;
        if(sub.getStatusChange() != null) {
            statusChangeIterator = sub.getStatusChange().iterator();
            ArrayList<StatusChangesJsonObject> statusChangesJsonObjectArrayList = new ArrayList<>();
            while(statusChangeIterator.hasNext()){
                StatusChange stchngTemp = statusChangeIterator.next();
                StatusChangesJsonObject statchngRetVal = new StatusChangesJsonObject();
                statchngRetVal.setEsmd_transaction_id(stchngTemp.getEsmdTransactionId());
                statchngRetVal.setStatus(stchngTemp.getStatus());
                statchngRetVal.setSplit_number(stchngTemp.getSplitInformation());
                statchngRetVal.setTitle(stchngTemp.getResult());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(stchngTemp.getCreateTs());
                statchngRetVal.setTime(strDate);
                statusChangesJsonObjectArrayList.add(statchngRetVal);
            }
            retVal.setStatus_changes(statusChangesJsonObjectArrayList);
        }
        return retVal;

    }

    //X12278Submission -PA

    private Hashtable<String, String> inputKey = new Hashtable<String, String>();
    //private Hashtable<Long, Object> err999Key = new Hashtable<Long, Object>();
    private Hashtable<String, String> err999Key = new Hashtable<String,String>();


    //New Code with excel_file_name as String input
    @PostMapping("/x12278submission")
    public @ResponseBody
    X12278CreateSubmissionResponseModel createX12278Submission(@RequestBody X12278CreateSubmissionRequestModel lb) {

        X12278CreateSubmissionResponseModel temp = new X12278CreateSubmissionResponseModel();
        CommitContext commitContext = new CommitContext();
        System.out.println("Inside Create X12278 Submission");

        if(lb.getIntended_recepient().isEmpty() || lb.getIntended_recepient() == null)
        {
            temp.setCall_error_code("1");
            temp.setCall_error_description("Intended Recepient Should not be NULL");
            return temp;
        }
        else{
            System.out.println("Intended Recepient: "+lb.getIntended_recepient());
            try {
                Recepient intdRecp = datamanager.loadValue(
                        "select o from rioc_Recepient o " +
                                "where o.oid = :recep ", Recepient.class)
                        .parameter("recep", lb.getIntended_recepient().trim())
                        .one();
                if (intdRecp == null) {
                    temp.setCall_error_code("2");
                    temp.setCall_error_description("Provided Intended Recepient not found");
                    return temp;
                }
                else{
                    try {
                        if (!lb.getPa_document().isEmpty() && lb.getPa_document().size() > 0) {
                            //Creating X12278 Submission Details to DB
                            X12278Submission x12278subTemp = datamanager.create(X12278Submission.class);
                            x12278subTemp.setIntendedRecepient(intdRecp);

                            datamanager.commit(x12278subTemp);
                            System.out.println("X12278Submission Entity ID: " + x12278subTemp.getId());

                            //Adding Documents in PA Document to DB
                            List<PADocument> docSubSet = new ArrayList<>();
                            Iterator<PaDocumentSetJsonObject> dociterator = lb.getPa_document().iterator();
                            while (dociterator.hasNext()) {
                                DocumentType docType = null;
                                SupportedLanguage supLangType = null;
                                PADocument docTemp = datamanager.create(PADocument.class);
                                PaDocumentSetJsonObject dtJsonTemp = dociterator.next();
                                docTemp.setFilename(dtJsonTemp.getFilename());
                                docTemp.setTitle(dtJsonTemp.getTitle());
                                docTemp.setComments(dtJsonTemp.getComments());
                                docTemp.setLanguage(supLangType.fromId(dtJsonTemp.getLanguage()));
                                docTemp.setDocument_type(docType.fromId(dtJsonTemp.getDocument_type()));
                                docTemp.setX12278submissionID(x12278subTemp);
                                commitContext.addInstanceToCommit(docTemp);
                                datamanager.commit(docTemp);
                                docSubSet.add(docTemp);
                                System.out.println("Provided Document File Name: "+docTemp.getFilename());
                                System.out.println("Provided Document Title: "+docTemp.getTitle());
                                System.out.println("Provided Document Comments: "+docTemp.getComments());
                                System.out.println("Provided Document Language: "+docTemp.getLanguage());
                                System.out.println("Provided Document Document_type: "+docTemp.getDocument_type());
                                System.out.println("Provided Document X12278submissionID: "+docTemp.getX12278submissionID());
                            }
                            x12278subTemp.setPaDocument(docSubSet);
                            System.out.println("X12278 PA Document Details: "+x12278subTemp.getPaDocument());

                            System.out.println("X12278 Submission ID: "+x12278subTemp.getId().toString());
                            temp.setX12278submission_id(x12278subTemp.getId().toString());
                            return temp;

                        } else {
                            temp.setCall_error_code("1");
                            temp.setCall_error_description("Document not found");
                            return temp;
                        }
                    }
                    catch(Exception e)
                    {
                        temp.setCall_error_code("3");
                        temp.setCall_error_description(e.getMessage());
                        return temp;
                    }

                }
            }
            catch(Exception e)
            {
                temp.setCall_error_code("2");
                temp.setCall_error_description("Provided Intended Recepient is Invalid or not found ");
                return temp;
            }
        }
    }


    @RequestMapping(value="/x12278submission/{id}", method= POST)
    public @ResponseBody
    X12278SubmissionResponseModel PostX12278Submission(@PathVariable Long id,@ModelAttribute PostX12278SubmissionRequest tempReqObj){

        CommitContext commitContext = new CommitContext();
        X12278SubmissionResponseModel retVal = new X12278SubmissionResponseModel();
        List<MultipartFile> localFiles = tempReqObj.getUploadFiles();
        MultipartFile inputFile = tempReqObj.getExcel_file();
        X12278Submission x12sub = null;

        retVal.setX12278submission_id(id.toString());

        //Checking given Input request is valid or not
        try {
            x12sub = datamanager.load(X12278Submission.class).id(id).view("x12278Submission-view").one();
            //err999Key.get(id);
            //System.out.println("Error 999 Key HashTable: "+err999Key);

        }catch(Exception e){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        if(x12sub == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the submission Id provided");
            return retVal;
        }
        if(localFiles==null || (localFiles.size() ==0)){
            retVal.setCall_error_code("1");
            retVal.setCall_error_description("You should submit at least one Document file");
            return retVal;
        }
        List<PADocument> docObj = x12sub.getPaDocument();
        if(docObj.size() != localFiles.size())
        {
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("Incorrect number of Documents sent");
            return retVal;
        }

        if(inputFile==null || inputFile.isEmpty())
        {
            retVal.setCall_error_code("1");
            retVal.setCall_error_description("You should upload Input File");
            return retVal;
        }

        System.out.println("Provided Recepient EDI ID: "+x12sub.getIntendedRecepient().getEdiID());


        // Parsing provided Input Excel File
        if (x12sub.getIntendedRecepient().getEdiID() != null && !x12sub.getIntendedRecepient().getEdiID().isEmpty()) {
            String iRecEDI = x12sub.getIntendedRecepient().getEdiID();

            System.out.println("Input File Size: "+inputFile.getSize());

            if (inputFile.getSize() != 0) {

                System.out.println("Input File Name: "+inputFile.getOriginalFilename());
                System.out.println("Input File Extension: "+FilenameUtils.getExtension(inputFile.getOriginalFilename()));
                if (FilenameUtils.getExtension(inputFile.getOriginalFilename()).matches("xlsx|xls")) {
                    try {
                        File file = new File(inputFile.getOriginalFilename());
                        System.out.println("converted FIle Name: "+file.getName());
                        //file.createNewFile();
                        inputFile.transferTo(file);
                        System.out.println("COnverted File Size: "+file.length());

                        inputKey = createHashTable(file);
                        System.out.println("Input Hash Table: " + inputKey);

                        if (inputKey == null || inputKey.isEmpty()) {
                            System.out.println("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Reupload");
                            retVal.setCall_error_code("4");
                            retVal.setCall_error_description("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Reupload");
                            return retVal;
                        } else if (inputKey.size() > 0) {
                            String requesterNPI = inputKey.get("RequesterRequesterNPI");
                            if (requesterNPI == null || requesterNPI.isEmpty()) {
                                System.out.println("RequesterRequesterNPI Field is empty, Missing Field in the provided Excel File");
                                retVal.setCall_error_code("5");
                                retVal.setCall_error_description("RequesterRequesterNPI Field is empty, Missing Field in provided the Excel File");
                                return retVal;
                            } else {
                                try {
                                    System.out.println("Requested requesterNPI: " + requesterNPI);

                                    //Calling Service for Parsing the HashTable
                                    X12278ValidationStatus x12278validstus = AppBeans.get(SubmissionService.class).ParseHashtable(inputKey, iRecEDI, requesterNPI);
                                    System.out.println("Parse Validation Status of Uploaded Document:");
                                    System.out.println("------------------------------------------------");
                                    System.out.println("Validation Status: " + x12278validstus.getStatus());
                                    System.out.println("Validation Status Code: " + x12278validstus.getStatusCode());
                                    System.out.println("Validation Error Message: " + x12278validstus.getErrorMessage());

                                    if (x12278validstus.getStatusCode() == 0 && x12278validstus.getStatus().equalsIgnoreCase("Success")) {

                                        System.out.println("Validation Error 999 HashTable: " + x12278validstus.getError999Key());
                                        System.out.println("EDI String: " + x12278validstus.getEDI());
                                        System.out.println("PWK: " + x12278validstus.getPwk());

                                        EDI = x12278validstus.getEDI();

                                        if (EDI != null && !EDI.isEmpty()) {
                                            String status = x12278validstus.getStatus();
                                            UUID uuid = UUID.randomUUID();
                                            x12sub.setAttachmentControlNumber(x12278validstus.getPwk());
                                            x12sub.setX12UnqID(uuid.toString());

                                            err999Key = x12278validstus.getError999Key();

                                            //datamanager.commit(x12sub);

                                            System.out.println("X12278 Submission Unique ID: " + x12sub.getX12UnqID());
                                            System.out.println("Document Attachment Control Number: " + x12sub.getAttachmentControlNumber());
                                            //System.out.println("X12278 Submission Unique ID: " + x12278subTemp.getEdiID());
                                            //commitContext.addInstanceToCommit(x12278subTemp);
                                            //datamanager.commit(x12278subTemp);

                                        }

                                    } else if (x12278validstus.getStatusCode() == 1) {
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(x12278validstus.getErrorMessage());
                                        return retVal;
                                    } else if (x12278validstus.getStatusCode() == 2) {
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(x12278validstus.getErrorMessage());
                                        return retVal;
                                    } else if (x12278validstus.getStatusCode() == 3) {
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(x12278validstus.getErrorMessage());
                                        return retVal;
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace(); }
                            }

                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description("Input Excel File conversion/Loading Failed from Multipart File to File");
                        return retVal;
                    }
                }
                //Not Valid Input File
                else {
                    System.out.println("Input File is invalid, should be Excel Sheet");
                    retVal.setCall_error_code("2");
                    retVal.setCall_error_description("Input File is invalid, should be Excel Sheet");
                    return retVal;
                }
            }
            //input File size is 0
            else {
                System.out.println("Input File is Empty or Incorrect");
                retVal.setCall_error_code("2");
                retVal.setCall_error_description("Input File is Empty or Incorrect");
                return retVal;
            }

        }
        //NO EDIID for available for provided Intendent Recepient
        else {
            System.out.println("EDI ID is not found for provided Recepient ");
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("EDI ID is not found for provided Recepient");
            return retVal;
        }

        //End of Parsing Input FIle


        //Updating Uploaded Documents to PA Document File Descriptor
        System.out.println(localFiles.size());
        Iterator<MultipartFile> filesIt = localFiles.iterator();
        HashMap<String,MultipartFile> dupFileUpload = new HashMap<>();
        HashMap<String,PADocument> dupDocUpload = new HashMap<>();
        while(filesIt.hasNext()){

            Iterator<PADocument> docsetItr = docObj.iterator();
            MultipartFile tmpFile = filesIt.next();
            while(docsetItr.hasNext()){
                PADocument tempDt = docsetItr.next();
                System.out.println("CHecking provided File Details and uploaded files are same or not");
                System.out.println("------------------------------------------------------------------");
                System.out.println("Uploaded File Name: "+tmpFile.getOriginalFilename());
                System.out.println("PA Document File Name: "+tempDt.getFilename());
                if(tempDt.getFilename().equals(tmpFile.getOriginalFilename())){

                    if(!dupFileUpload.containsKey(tempDt.getId().toString())) {
                        dupFileUpload.put(tempDt.getId().toString(), tmpFile);
                        dupDocUpload.put(tempDt.getId().toString(), tempDt);
                        break;
                    }

                }
            }
        }
        if(dupFileUpload.size() != localFiles.size())
        {
            retVal.setCall_error_code("4");
            retVal.setCall_error_description("File not found in the document list");
            return retVal;
        }

        for (Map.Entry me : dupFileUpload.entrySet()) {
            MultipartFile locFile = (MultipartFile) me.getValue();
            System.out.println("Uploaded File Name: "+locFile.getOriginalFilename());
            PADocument locDt = (PADocument) dupDocUpload.get(me.getKey());
            FileDescriptor fileDescriptor = datamanager.create(FileDescriptor.class);
            fileDescriptor.setName(locFile.getOriginalFilename());
            System.out.println("File Extension: " + FilenameUtils.getExtension(locFile.getOriginalFilename()));
            fileDescriptor.setExtension(FilenameUtils.getExtension(locFile.getOriginalFilename()));
            //fileDescriptor.setExtension("pdf"); // As we will be supporting only pdf
            fileDescriptor.setCreateDate(new Date());
            try {

                //byte[] bytes = locFile.getBytes();
                fileDescriptor.setSize((long) locFile.getSize());
                InputStream is = locFile.getInputStream();
                fileLoader.saveStream(fileDescriptor, () -> is);
            } catch (FileStorageException | IOException e) {
                System.out.println("error occured");
                e.printStackTrace();
                retVal.setCall_error_code("4");
                retVal.setCall_error_description(e.getMessage());
                return retVal;
            }
            datamanager.commit(fileDescriptor);
            locDt.setFileDescriptor(fileDescriptor);
            datamanager.commit(locDt);

        }
        //datamanager.commit(x12sub);

        //X12278Submission to the ESMD
        if(!err999Key.isEmpty()) {
            submitX12278Document(x12sub, retVal, EDI, err999Key);
        }


    return retVal;
    }


    public void submitX12278Document(X12278Submission x12sub,X12278SubmissionResponseModel retVal,String EDIString, Hashtable<String,String> err999KeyVal) {
        //X12278SubmissionResponseModel retVal = new X12278SubmissionResponseModel();
        System.out.println("Sending Document to ESMD from Portal");
        System.out.println("-------------------------------------");
        System.out.println("X12278Submission: "+x12sub);
        System.out.println("EDI String: "+EDIString);
        System.out.println("Error 999 Key: "+err999KeyVal);
        try {
            X12278SubmissionStatus x12278subStatus = AppBeans.get(SubmissionService.class).SubmitX12278Submission(x12sub, EDIString, err999KeyVal);

            //retVal.setX12278submission_id(x12sub.getId().toString());

            String status = x12278subStatus.getErrorMessage();
            CommitContext commitContext = new CommitContext();
            System.out.println("Status - Message From ESMD: " + status);

            if (status != null && !status.isEmpty()) {
                if (x12278subStatus.getErrorCode().equalsIgnoreCase("-1")) {

                    try {
                        x12sub.setStatus("FAILED");

                        //CommitContext commitContext = new CommitContext();
                        PAError paErrTemp = datamanager.create(PAError.class);
                        System.out.println(" PA Error Trans Type: " + x12278subStatus.getTransType());
                        System.out.println(" PA Error Description: " + x12278subStatus.getErrorMessage());
                        System.out.println(" PA Error Code: " + x12278subStatus.getErrorCode());
                        paErrTemp.setX12278submissionID(x12sub);
                        paErrTemp.setTransType(x12278subStatus.getTransType());
                        paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                        paErrTemp.setErrorCode(x12278subStatus.getErrorCode());
                        commitContext.addInstanceToCommit(paErrTemp);

                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code(x12278subStatus.getErrorCode());
                        retVal.setCall_error_description(x12278subStatus.getErrorMessage());
                        retVal.setTransType(x12278subStatus.getTransType());

                        //return retVal;
                    }
                    catch(Exception e)
                    {
                        System.out.println("error occured");
                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description(e.getMessage());
                        retVal.setTransType(x12278subStatus.getTransType());
                        e.printStackTrace();
                    }

                }

                //In Case of ESMD Server Failure i.e In Case of HIH Configuration is Null
                else if (x12278subStatus.getErrorCode().equalsIgnoreCase("Failure")) {

                    try {
                        x12sub.setStatus("FAILED");

                        //CommitContext commitContext = new CommitContext();
                        PAError paErrTemp = datamanager.create(PAError.class);
                        System.out.println(" PA Error Trans Type: " + x12278subStatus.getTransType());
                        System.out.println(" PA Error Description: " + x12278subStatus.getErrorMessage());
                        System.out.println(" PA Error Code: " + x12278subStatus.getErrorCode());
                        paErrTemp.setX12278submissionID(x12sub);
                        paErrTemp.setTransType(x12278subStatus.getTransType());
                        paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                        paErrTemp.setCodeContext(x12278subStatus.getErrorCode());
                        commitContext.addInstanceToCommit(paErrTemp);

                        retVal.setStatus("FAILED");
                        retVal.setTransType(x12278subStatus.getTransType());
                        retVal.setCall_error_code(x12278subStatus.getErrorCode());
                        retVal.setCall_error_description(x12278subStatus.getErrorMessage());

                        //return retVal;
                    }
                    catch(Exception e)
                    {
                        System.out.println("error occured");
                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description(e.getMessage());
                        retVal.setTransType(x12278subStatus.getTransType());
                        e.printStackTrace();
                    }
                }

                //In Case of Internal Errors
                else if (status.equalsIgnoreCase("Internal Error")) {

                    try {
                        x12sub.setStatus("FAILED");

                        //CommitContext commitContext = new CommitContext();
                        PAError paErrTemp = datamanager.create(PAError.class);
                        System.out.println(" PA Error Trans Type: " + x12278subStatus.getTransType());
                        System.out.println(" PA Error Description: " + x12278subStatus.getErrorMessage());
                        System.out.println(" PA Error Context: " + x12278subStatus.getErrorCode());
                        paErrTemp.setX12278submissionID(x12sub);
                        paErrTemp.setTransType(x12278subStatus.getTransType());
                        paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                        paErrTemp.setCodeContext(x12278subStatus.getErrorCode());
                        commitContext.addInstanceToCommit(paErrTemp);

                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code(x12278subStatus.getErrorCode());
                        retVal.setCall_error_description(x12278subStatus.getErrorMessage());
                        retVal.setTransType(x12278subStatus.getTransType());

                        //return retVal;
                    }
                    catch(Exception e)
                    {
                        System.out.println("error occured");
                        e.printStackTrace();
                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description(e.getMessage());
                        retVal.setTransType(x12278subStatus.getTransType());
                    }
                }

                // In Case OF ERROR on 999 After Submission TO ESMD Connect
                if (status.equalsIgnoreCase("Error on 999")) {
                    System.out.println("ERROR on 999 X12278 Submission Edit Logic");
                    System.out.println("--------------------------------------------");
                    System.out.println("Checking Parse on ERROR on 999 Segment Error Length: " + x12278subStatus.getSegErrs().size());
                    try {
                        x12sub.setStatus("FAILED");
                        retVal.setStatus("FAILED");
                        //retVal.setSegErrs(x12278subStatus.getSegErrs());
                        //this.dataManager.commit(x12sub);
                        //setting data into PA ERROR Entity
                        ArrayList<ErrorInfo> paErrlist = x12278subStatus.getSegErrs();

                        if (x12278subStatus.getSegErrs() != null) {
                            Iterator<ErrorInfo> errinfoIt = x12278subStatus.getSegErrs().iterator();

                            ArrayList<X12278ErrorJsonObject> x12ErrorJsonObjArrayList = new ArrayList<>();


                            while (errinfoIt.hasNext()) {
                                try {
                                    //CommitContext commitContext = new CommitContext();
                                    PAError paErrTemp = datamanager.create(PAError.class);
                                    ErrorInfo errIn = errinfoIt.next();
                                    X12278ErrorJsonObject errRetVal = new X12278ErrorJsonObject();
                                    System.out.println("Error Context: " + errIn.getErrorCtx());
                                    System.out.println("Error Code: " + errIn.getErrorCode());
                                    System.out.println("Error Description: " + errIn.getErrorDesp());
                                    System.out.println(" PA Error Trans TYpe: " + x12278subStatus.getTransType());
                                    paErrTemp.setX12278submissionID(x12sub);
                                    paErrTemp.setTransType(x12278subStatus.getTransType());
                                    paErrTemp.setErrorCode(errIn.getErrorCode());
                                    paErrTemp.setDescription(errIn.getErrorDesp());
                                    paErrTemp.setCodeContext(errIn.getErrorCtx());
                                    commitContext.addInstanceToCommit(paErrTemp);

                                    errRetVal.setTrans_type(x12278subStatus.getTransType());
                                    errRetVal.setError_code(errIn.getErrorCode());
                                    errRetVal.setError_description(errIn.getErrorDesp());
                                    errRetVal.setError_context(errIn.getErrorCtx());

                                    //dataManager.commit(commitContext);
                                    //this.dataManager.commit(commitContext);
                                    x12ErrorJsonObjArrayList.add(errRetVal);
                                }
                                catch(Exception e){
                                    System.out.println("error occured");
                                    e.printStackTrace();
                                    retVal.setStatus("FAILED");
                                    retVal.setCall_error_code("4");
                                    retVal.setCall_error_description(e.getMessage());
                                    retVal.setTransType(x12278subStatus.getTransType());
                                }
                            }
                            retVal.setErrJsonObjs(x12ErrorJsonObjArrayList);
                        }
                        //return retVal;
                    }
                    catch(Exception e)
                    {
                        System.out.println("error occured");
                        e.printStackTrace();
                        retVal.setStatus("FAILED");
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description(e.getMessage());
                        retVal.setTransType(x12278subStatus.getTransType());
                    }
                }

                // In Case OF Success Submission TO ESMD Connect x12278subStatus.getErrorCode().equalsIgnoreCase("Success")
                else if (status.equalsIgnoreCase("No Error")) {
                    System.out.println(" In Success Submission to ESMD Connect");
                    System.out.println("------------------------------------------");
                    System.out.println(" Error Code: " + x12278subStatus.getErrorCode());
                    System.out.println("ESMD Transaction ID: " + x12278subStatus.getEsmdTransactionId());

                    try {
                        x12sub.setEsmdTransactionId(x12278subStatus.getEsmdTransactionId());
                        //this.dataManager.commit(x12sub);

                        System.out.println("Insert into PA STATUS CHANGE DataBase for X12278 Submission PA Request-Response to ESMD");
                        System.out.println(" PA Status Change Trans Type: " + x12278subStatus.getTransType());

                        //CommitContext commitContext2 = new CommitContext();
                        PAStatusChange paStsChng = datamanager.create(PAStatusChange.class);
                        paStsChng.setX12278submissionID(x12sub);
                        //paStsChng.setTransType("X12 Transaction");
                        paStsChng.setTransType(x12278subStatus.getTransType());
                        paStsChng.setEsmdTransactionId(x12278subStatus.getEsmdTransactionId());
                        paStsChng.setResult("Success");
                        paStsChng.setStatus("PA Request-Response Success");

                        //commitContext2.addInstanceToCommit(paStsChng);
                        commitContext.addInstanceToCommit(paStsChng);
                        //dataManager.commit(commitContext2);
                        //this.dataManager.commit(commitContext1);

                        //x12sub.setStatus("PA Request-Response Success");

                        retVal.setEsmd_transaction_id(x12278subStatus.getEsmdTransactionId());
                        retVal.setTransType(x12278subStatus.getTransType());

                        System.out.println("Segment Status Information Size: " + x12278subStatus.getSegStatus().size());
                        ArrayList<StatusInfo> paStslist = x12278subStatus.getSegStatus();

                        Iterator<StatusInfo> statsInfoIt = x12278subStatus.getSegStatus().iterator();


                        while (statsInfoIt.hasNext()) {

                            try {
                                StatusInfo stsIn = statsInfoIt.next();
                                System.out.println("Transaction Type: " + stsIn.getTransType());
                                System.out.println("PA Document set Submission Status Code: " + stsIn.getStatusCode());

                                System.out.println("PA Document Unique ID: " + stsIn.getUniqueID());
                                x12sub.setDocUnqID(stsIn.getUniqueID());
                                retVal.setDoc_Unq_id(stsIn.getUniqueID());
                                System.out.println("X12278Submission Document Unique ID: " + x12sub.getDocUnqID());

                                if (stsIn.getStatusCode().equalsIgnoreCase("2")) {
                                    try {
                                        System.out.println("ERROR While Document Submission with Error Code: 2");
                                        System.out.println("-----------------------------------------------------");
                                        System.out.println("Checking ERROR while Document Submission Segment Error Length: " + x12278subStatus.getSegErrs().size());

                                        x12sub.setStatus("FAILED");
                                        retVal.setStatus("FAILED");

                                        x12sub.setSuppDocMessage("FAILED");
                                        retVal.setSUPP_DOC_MESSAGE("FAILED");
                                        //retVal.setSegErrs(x12278subStatus.getSegErrs());

                                        //setting data into PA ERROR Entity
                                        ArrayList<ErrorInfo> paErrlist = x12278subStatus.getSegErrs();

                                        Iterator<ErrorInfo> errinfoIt = x12278subStatus.getSegErrs().iterator();

                                        ArrayList<X12278ErrorJsonObject> x12ErrorJsonObjArrayList = new ArrayList<>();

                                        while (errinfoIt.hasNext()) {
                                            try {
                                                ErrorInfo errIn = errinfoIt.next();
                                                X12278ErrorJsonObject errRetVal = new X12278ErrorJsonObject();
                                                System.out.println("Error Context: " + errIn.getErrorCtx());
                                                System.out.println("Error Code: " + errIn.getErrorCode());
                                                System.out.println("Error Description: " + errIn.getErrorDesp());
                                                System.out.println("Error Severity: " + errIn.getSeverity());
                                                System.out.println("Error TransType: " + errIn.getTransType());
                                                //CommitContext commitContext = new CommitContext();
                                                PAError paErrTemp = datamanager.create(PAError.class);
                                                //System.out.println(" PA Error Trans TYpe: "+x12278subStatus.getTransType());
                                                paErrTemp.setX12278submissionID(x12sub);
                                                paErrTemp.setCodeContext(errIn.getErrorCtx());
                                                paErrTemp.setErrorCode(errIn.getErrorCode());
                                                paErrTemp.setDescription(errIn.getErrorDesp());
                                                paErrTemp.setSeverity(errIn.getSeverity());
                                                paErrTemp.setTransType(errIn.getTransType());
                                                //paErrTemp.setTransType(TransType.X12);

                                                //commitContext2.addInstanceToCommit(paErrTemp);
                                                commitContext.addInstanceToCommit(paErrTemp);

                                                errRetVal.setError_context(errIn.getErrorCtx());
                                                errRetVal.setError_code(errIn.getErrorCode());
                                                errRetVal.setError_description(errIn.getErrorDesp());
                                                errRetVal.setSeverity(errIn.getSeverity());
                                                errRetVal.setTrans_type(errIn.getTransType());

                                                //dataManager.commit(commitContext);
                                                //this.dataManager.commit(commitContext);
                                                //x12sub.setSuppDocMessage("FAILED");
                                                //retVal.setSUPP_DOC_MESSAGE("FAILED");

                                                x12ErrorJsonObjArrayList.add(errRetVal);
                                            } catch (Exception e) {
                                                System.out.println("error occured");
                                                retVal.setStatus("FAILED");
                                                retVal.setSUPP_DOC_MESSAGE("FAILED");
                                                retVal.setCall_error_code("4");
                                                retVal.setCall_error_description(e.getMessage());
                                                retVal.setTransType(stsIn.getTransType());
                                                e.printStackTrace();
                                            }
                                        }
                                        retVal.setErrJsonObjs(x12ErrorJsonObjArrayList);

                                    } catch (Exception e) {
                                        System.out.println("error occured");
                                        retVal.setStatus("FAILED");
                                        retVal.setSUPP_DOC_MESSAGE("FAILED");
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(e.getMessage());
                                        retVal.setTransType(stsIn.getTransType());
                                        e.printStackTrace();
                                    }

                                }
                                else if (stsIn.getStatusCode().equalsIgnoreCase("1")) {
                                    System.out.println("ERROR While Document Submission with Error Code: 1");
                                    System.out.println("-----------------------------------------------------");

                                    try {
                                        x12sub.setStatus("FAILED");
                                        x12sub.setSuppDocMessage("FAILED");

                                        //CommitContext commitContext = new CommitContext();
                                        PAError paErrTemp = datamanager.create(PAError.class);
                                        System.out.println(" PA Error Trans TYpe: " + stsIn.getTransType());
                                        paErrTemp.setX12278submissionID(x12sub);
                                        paErrTemp.setCodeContext(stsIn.getResult());
                                        paErrTemp.setErrorCode(stsIn.getStatusCode());
                                        paErrTemp.setDescription(stsIn.getStatus());
                                        //paErrTemp.setSeverity();
                                        paErrTemp.setTransType(stsIn.getTransType());

                                        commitContext.addInstanceToCommit(paErrTemp);

                                        //dataManager.commit(commitContext);
                                        //this.dataManager.commit(commitContext);

                                        //x12sub.setSuppDocMessage(paStslist.get(x).getResult());


                                        retVal.setStatus("FAILED");
                                        retVal.setSUPP_DOC_MESSAGE("FAILED");
                                        retVal.setCall_error_code(stsIn.getStatusCode());
                                        retVal.setCall_error_description(stsIn.getStatus());
                                        retVal.setTransType(stsIn.getTransType());
                                        retVal.setError_code_context(stsIn.getResult());

                                        //return retVal;

                                    } catch (Exception e) {
                                        System.out.println("error occured");
                                        retVal.setStatus("FAILED");
                                        retVal.setSUPP_DOC_MESSAGE("FAILED");
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(e.getMessage());
                                        retVal.setTransType(stsIn.getTransType());
                                        e.printStackTrace();
                                    }

                                }
                                else if (stsIn.getStatusCode().equalsIgnoreCase("0")) {
                                    try {
                                        x12sub.setStatus("SUCCESS");
                                        retVal.setStatus("SUCCESS");
                                        x12sub.setSuppDocMessage(stsIn.getResult());
                                        retVal.setSUPP_DOC_MESSAGE(stsIn.getResult());

                                        ArrayList<X12278StatusChangeJsonObject> x12StsChngJsonObjArrayList = new ArrayList<>();

                                        X12278StatusChangeJsonObject stsChngVal = new X12278StatusChangeJsonObject();
                                        //CommitContext commitContext1 = new CommitContext();
                                        PAStatusChange paStChngTemp = datamanager.create(PAStatusChange.class);
                                        System.out.println(" PA Status Change Trans Type: " + stsIn.getTransType());
                                        System.out.println(" PA Status Change Result: " + stsIn.getResult());
                                        System.out.println(" PA Status Change Status: " + stsIn.getStatus());
                                        //System.out.println(" PA Status Change Esmd Transaction ID: "+stsIn.getEsmdTransactionId());
                                        System.out.println(" Document Unique ID: " + stsIn.getUniqueID());
                                        paStChngTemp.setX12278submissionID(x12sub);
                                        paStChngTemp.setTransType(stsIn.getTransType());
                                        paStChngTemp.setResult(stsIn.getResult());
                                        paStChngTemp.setStatus(stsIn.getStatus());
                                        //paStChngTemp.setEsmdTransactionId(stsIn.getEsmdTransactionId());
                                        //x12sub.setDocUnqID(paStslist.get(x).getUniqueID());

                                        //commitContext2.addInstanceToCommit(paStChngTemp);
                                        commitContext.addInstanceToCommit(paStChngTemp);
                                        //datamanager.commit(paStChngTemp);

                                        stsChngVal.setTrans_type(stsIn.getTransType());
                                        stsChngVal.setResult(stsIn.getResult());
                                        stsChngVal.setStatus(stsIn.getStatus());

                                        try {
                                            System.out.println("PA Status Change Create Time: " + paStChngTemp.getCreateTs());
                                            //stsChngVal.setEsmd_transaction_id(stsIn.getEsmdTransactionId());
                                            //DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS:ss");
                                        /*
                                        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                        String stdate = simpleDateFormat.format(paStChngTemp.getCreateTs());
                                        System.out.println(stdate);
                                        */

                                            //DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:SS.sss");2019-11-04 14:45:03.464
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                            String strDate = dateFormat.format(paStChngTemp.getCreateTs());
                                            stsChngVal.setTime(strDate);
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                        x12StsChngJsonObjArrayList.add(stsChngVal);

                                        retVal.setStaChngJsonObjs(x12StsChngJsonObjArrayList);


                                        retVal.setTransType(stsIn.getTransType());
                                        //retVal.setCall_error_description(stsIn.getStatus());
                                        //retVal.setError_code_context(stsIn.getResult());

                                        //return retVal;
                                    } catch (Exception e) {
                                        System.out.println("error occured");
                                        retVal.setStatus("SUCCESS");
                                        retVal.setTransType(stsIn.getTransType());
                                        retVal.setSUPP_DOC_MESSAGE(stsIn.getResult());
                                        retVal.setCall_error_code("4");
                                        retVal.setCall_error_description(e.getMessage());
                                        e.printStackTrace();
                                    }
                                }

                            }catch(Exception e)
                                {
                                    System.out.println("error occured");
                                    retVal.setCall_error_code("4");
                                    retVal.setCall_error_description(e.getMessage());
                                    retVal.setTransType(statsInfoIt.next().getTransType());
                                    e.printStackTrace();
                                }
                            }
                        }
                    catch(Exception e)
                    {
                        System.out.println("error occured");
                        retVal.setCall_error_code("4");
                        retVal.setCall_error_description(e.getMessage());
                        retVal.setTransType(x12278subStatus.getTransType());
                        e.printStackTrace();
                    }
                }

            }

            else if (status == null && status.isEmpty()) {
                x12sub.setStatus("Server Down");
                retVal.setStatus("Server Down");
                System.out.println("status is null");
                //return retVal;
            }
            datamanager.commit(commitContext);
        }
        catch(Exception e)
        {
            System.out.println("error occured");
            e.printStackTrace();
            retVal.setCall_error_code("4");
            retVal.setCall_error_description(e.getMessage());
        }

        datamanager.commit(x12sub);

    }


    // End of New code

    //Creating HashTable from provided EXCEL ( .xlsx and .xls ) SpreedSheet File
    protected Hashtable<String, String> createHashTable(File file) {

        System.out.println("Inside HashTable");
        FileInputStream fileIn = null;
        Workbook workbook = null;
        boolean bError = false;
        try {
            System.out.println("Inside Try HashTable");
            fileIn = new FileInputStream(file);
            //Workbook workbook = WorkbookFactory.create(file,"x12278pwd");
            workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter dataFormatter = new DataFormatter();

            // 1. You can obtain a rowIterator and columnIterator and iterate over them
            System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Now let's iterate over the columns of the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                int count = 0;
                String key = "", value = "";
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String cellValue = dataFormatter.formatCellValue(cell);
                    String trimmerVal = cellValue.trim();
                    //    System.out.print(trimmerVal + "\t");
                    if (count == 0) {
                        key = trimmerVal;
                    } else {
                        value = trimmerVal;
                    }
                    count++;
                }
                if (!value.equals("")) {
                    inputKey.put(key, value);
                    System.out.println("key is: " + key + "value is: " + value);
                }
            }

            workbook.close();
            fileIn.close();

            System.out.println("End of Hash Table Creation");
        } catch (FileNotFoundException e) {
            bError = true;
            e.printStackTrace();
        } catch (IOException e) {
            bError = true;
            e.printStackTrace();
        } catch (NullPointerException e) {
            bError = true;
            e.printStackTrace();
        } catch (Exception e) {
            bError = true;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        if (bError) {
            System.out.println("After Exception:");
            System.out.println("-------------------");
            try {
                if (workbook != null) {
                    workbook.close();
                }

                if (fileIn != null || fileIn.available() > 0) {
                    fileIn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputKey;
    }


    @RequestMapping(value="/x12278submission/{id}", method= GET)
    public @ResponseBody
    X12278SubmissionGetStatusRespModel PostX12278Submission(@PathVariable Long id) {
        X12278SubmissionGetStatusRespModel retVal = new X12278SubmissionGetStatusRespModel();

        retVal.setSubmission_id(id.toString());
        X12278Submission x12278sub = null;
        try {
            x12278sub = datamanager.load(X12278Submission.class).id(id).view("x12278Submission-view").one();
        }catch(Exception e){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the X12278Submission Id provided");
            return retVal;
        }
        if(x12278sub == null){
            retVal.setCall_error_code("2");
            retVal.setCall_error_description("No records found for the X12278Submission Id provided");
            return retVal;
        }

        retVal.setStatus(x12278sub.getStatus());
        retVal.setDocument_submission_status(x12278sub.getSuppDocMessage());

        //InCase of Errors
        Iterator<PAError> errorIterator ;
        if(x12278sub.getPaError() != null) {
            errorIterator = x12278sub.getPaError().iterator();
            ArrayList<X12278ErrorJsonObject> errorJsonObjectArrayList = new ArrayList<>();
            while(errorIterator.hasNext()){
                PAError errTemp = errorIterator.next();
                X12278ErrorJsonObject errRetVal = new X12278ErrorJsonObject();
                errRetVal.setError_code(errTemp.getErrorCode());
                errRetVal.setSeverity(errTemp.getSeverity());
                errRetVal.setError_context(errTemp.getCodeContext());
                errRetVal.setError_description(errTemp.getDescription());
                //errRetVal.setError_description(errTemp.getError());
                errRetVal.setEsmd_transaction_id(errTemp.getEsmdTransactionId());
                errRetVal.setTrans_type(errTemp.getTransType());
                errorJsonObjectArrayList.add(errRetVal);
            }
            retVal.setErrors(errorJsonObjectArrayList);
        }

        //InCase of Successfull submission
        Iterator<PAStatusChange> statusChangeIterator ;
        if(x12278sub.getPaStatusChange() != null) {

            retVal.setDocument_unique_id(x12278sub.getDocUnqID());
            retVal.setEsmd_transaction_id(x12278sub.getEsmdTransactionId());

            statusChangeIterator = x12278sub.getPaStatusChange().iterator();
            ArrayList<X12278StatusChangeJsonObject> statusChangesJsonObjectArrayList = new ArrayList<>();
            while(statusChangeIterator.hasNext()){
                PAStatusChange stchngTemp = statusChangeIterator.next();
                X12278StatusChangeJsonObject statchngRetVal = new X12278StatusChangeJsonObject();
                statchngRetVal.setEsmd_transaction_id(stchngTemp.getEsmdTransactionId());
                statchngRetVal.setStatus(stchngTemp.getStatus());
                statchngRetVal.setResult(stchngTemp.getResult());
                statchngRetVal.setTrans_type(stchngTemp.getTransType());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(stchngTemp.getCreateTs());
                statchngRetVal.setTime(strDate);
                statusChangesJsonObjectArrayList.add(statchngRetVal);
            }
            retVal.setStatus_changes(statusChangesJsonObjectArrayList);
        }

        return retVal;
    }
    //End of Get Method



}

