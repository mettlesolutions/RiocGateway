package com.mettles.rioc.portal.customcontrollers;

import com.mettles.rioc.X12275SubmissionStatus;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.entity.Error;
import com.mettles.rioc.portal.jsonobjects.*;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.portal.App;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.mettles.rioc.service.SubmissionService;
import org.springframework.web.multipart.MultipartFile;
;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
}

