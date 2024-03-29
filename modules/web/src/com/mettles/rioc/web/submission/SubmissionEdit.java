package com.mettles.rioc.web.submission;

import com.haulmont.cuba.gui.components.actions.EditAction;
import com.mettles.rioc.UiNotificationEvent;
import com.mettles.rioc.X12275SubmissionStatus;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.entity.Error;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.service.SubmissionService;
import com.haulmont.cuba.web.gui.components.table.TableDataContainer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;


import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@UiController("rioc_Submission.edit")
@UiDescriptor("submission-edit.xml")
@EditedEntityContainer("submissionDc")
@LoadDataBeforeShow
@SuppressWarnings("unchecked")
public class SubmissionEdit extends StandardEditor<Submission> {

    @Inject
    TextField stageField;
    @Inject
    Button btnsubmit;
    @Inject
    Table statusChangesTable;
    @Inject
    Table errorTable;
    @Inject
    Table notificationSlotsTable;
    @Inject
    Table documentSetTable;





    @Inject
    private LookupPickerField<LineofBusiness> purposeOfSubmissionField;

    @Inject
    TextField highestSpiltNoField;

    @Inject
    TextField lastSubmittedSplitField;

    @Inject
    private Notifications notifications;

    @Inject
    private TextField esMDClaimIDField;

    @Inject
    private TextField esmdCaseIdField;

    @Inject
    private CheckBox bSendinX12Field;


    @Inject
    private DataManager dataManager;



    //@Inject
    //TextField authorNPIField;
    @Inject
    InstanceLoader<Submission> submissionDl;

    @Inject
    InstanceContainer<Submission> submissionDc;


    @Inject
    CollectionPropertyContainer<Document> documentDc;
    @Inject
    CollectionPropertyContainer<StatusChange> statusChangeDc;
    @Inject
    CollectionPropertyContainer<Error> errorDc;
    @Inject
    CollectionPropertyContainer<NotificationSlot> notificationSlotDc;
    @Inject
    FileLoader fileLoader;

  //  @Inject
    //private SubmissionService subSrc;
    @Inject
    private Metadata metadata;



    public void sendDocumentinX12(){
        Submission sub = submissionDc.getItem();
        if(sub.getAutoSplit()){
            notifications.create().withCaption("Auto Split Can't be enabled for ").withType(Notifications.NotificationType.HUMANIZED).show();
            btnsubmit.setVisible(true);
            btnsubmit.setEnabled(true);
            return;
        }
        if(sub.getIntendedRecepient().getEdiID() == null){
            notifications.create().withCaption("This RC doesnt have EDI ID configured").withType(Notifications.NotificationType.HUMANIZED).show();
            btnsubmit.setVisible(true);
            btnsubmit.setEnabled(true);
            return;
        }
       if(StringUtils.isEmpty(sub.getEsMDClaimID())){
           notifications.create().withCaption("Claim id cannot be empty for PWK").withType(Notifications.NotificationType.HUMANIZED).show();
           btnsubmit.setVisible(true);
           btnsubmit.setEnabled(true);
           return;
       }
        if(sub.getDocument() == null){
            System.out.println("get document null");
            btnsubmit.setVisible(true);
            btnsubmit.setEnabled(true);
            return;
        }else{
            System.out.println("get document size is"+sub.getDocument().size());
            boolean bError = false;
            String attachmentControlNum= null;
            int splitNum = 0;

            long size = 0;

            if(sub.getDocument().size() > 0){
                Iterator<Document> documentIterator = sub.getDocument().iterator();
                while(documentIterator.hasNext()){
                    Document dtTemp = documentIterator.next();
                    if(dtTemp.getFileDescriptor() == null){
                        notifications.create().withCaption("Auto Split Can't be enabled for X12 Submissions ").withType(Notifications.NotificationType.HUMANIZED).show();
                        btnsubmit.setVisible(true);
                        btnsubmit.setEnabled(true);
                        return;
                    }else{
                        size = size + dtTemp.getFileDescriptor().getSize();
                    }
                    if(splitNum == 0){
                        splitNum = dtTemp.getSplitNumber();
                    }else if(dtTemp.getSplitNumber() != splitNum){
                        notifications.create().withCaption("Splits are not supported for X12 PWK ").withType(Notifications.NotificationType.HUMANIZED).show();
                        btnsubmit.setVisible(true);
                        btnsubmit.setEnabled(true);
                        return;
                    }
                    if(dtTemp.getAttachmentControlNumber()  ==  null || dtTemp.getAttachmentControlNumber().equals("")){
                        notifications.create().withCaption("Attachment Control Number cannot be null ").withType(Notifications.NotificationType.HUMANIZED).show();
                        btnsubmit.setVisible(true);
                        btnsubmit.setEnabled(true);
                        return;
                    }else{
                        if(attachmentControlNum  == null){
                            attachmentControlNum = dtTemp.getAttachmentControlNumber();
                        }else{
                            if(!attachmentControlNum.equals(dtTemp.getAttachmentControlNumber())){
                                notifications.create().withCaption("Attachment Control Number for all the documents needs to be same ").withType(Notifications.NotificationType.HUMANIZED).show();
                                btnsubmit.setVisible(true);
                                btnsubmit.setEnabled(true);
                                return;
                            }
                        }
                    }
                }
            }
            System.out.println("Total size is "+size);
            if(size > 150000000){
                notifications.create().withCaption("Total file size cannot be greater than 150MB ").withType(Notifications.NotificationType.HUMANIZED).show();
                btnsubmit.setVisible(true);
                btnsubmit.setEnabled(true);
                return;
            }else{
                submitX12275Document(sub);
            }
        }

    }

    public void submitX12275Document(Submission sub){

        X12275SubmissionStatus status = AppBeans.get(SubmissionService.class).SubmitX12275Submission(sub);
        CommitContext commitContext = new CommitContext();

        if(status.getErrorCode().equals("Success")){
            submissionDc.getItem().setStatus("Success");
            submissionDc.getItem().setStage("Submitted");
            submissionDc.getItem().setUniqueIdList(status.getUniqueID());
            notifications.create().withCaption("Submission Completed check the status table for Update").withType(Notifications.NotificationType.HUMANIZED).show();
        }else{
            submissionDc.getItem().setStatus(status.getErrorCode());
            submissionDc.getItem().setStage(status.getErrorMessage());
            submissionDc.getItem().setUniqueIdList(status.getUniqueID());
            notifications.create().withCaption("Submission Failed").withType(Notifications.NotificationType.HUMANIZED).show();
        }
        {
            System.out.println("Entered into get status change");
            Iterator<Document> documentIterator = documentDc.getItems().iterator();
            while(documentIterator.hasNext()){
                Document dtTemptoRem = documentIterator.next();

                    dataManager.remove(dtTemptoRem.getFileDescriptor());
                    try {
                        // dtTemptoRem.getFileDescriptor().
                        // fileLoader.
                        fileLoader.removeFile(dtTemptoRem.getFileDescriptor());
                    }catch(Exception e){
                        System.out.println("Exception Occured while removing the file");
                    }

                    dtTemptoRem.setFileDescriptor(null);
                    documentDc.replaceItem(dtTemptoRem);
                    // commitContext.addInstanceToCommit(dtTemptoRem);

            }
        }
        commitContext.addInstanceToCommit(submissionDc.getItem());
        dataManager.commit(commitContext);
    }


    public void onBtnsubmitClick() {
        String txt = "Submitted";

         this.commitChanges();

        Document dcTemp = null;
        //submissionDc.getItem().setDocument(documentDc.getItems());
        Submission sub = submissionDc.getItem();

        if(sub.getBSendinX12() || bSendinX12Field.isChecked()){
            sendDocumentinX12();
            return;
        }

        ArrayList<Document> docArryListSubmit = null;
        if(sub.getDocument() == null){
            System.out.println("get document null");
            btnsubmit.setVisible(true);
            btnsubmit.setEnabled(true);
            return;
        }else{
            System.out.println("get document size is"+sub.getDocument().size());
            if(sub.getDocument().size() == 0) {
                notifications.create().withCaption("Please upload document").withType(Notifications.NotificationType.HUMANIZED).show();
                btnsubmit.setVisible(true);
                btnsubmit.setEnabled(true);
                return;
            }

        }
        if(sub.getAutoSplit()){
            if(sub.getDocument().size() > 1){
                System.out.println("Only One Document needs to be attached when auto_split is enabled");
                notifications.create().withCaption("Please attach only one Document when auto split is enabled").withType(Notifications.NotificationType.HUMANIZED).show();
                btnsubmit.setVisible(true);
                btnsubmit.setEnabled(true);
                return;
            }else
                {

                Iterator<Document> documentIterator = documentDc.getItems().iterator();
                if(documentIterator.hasNext()) {
                    Document largeDoc = documentIterator.next();

                    if(largeDoc.getFileDescriptor() == null){
                        notifications.create().withCaption("please upload document").withType(Notifications.NotificationType.HUMANIZED).show();
                        btnsubmit.setVisible(true);
                        btnsubmit.setEnabled(true);
                    }

                   // documentDc.getMutableItems().remove(dcTemp);
                    if(largeDoc.getFileDescriptor().getSize() > 75000000) {
                        List<Document> docArryList = AppBeans.get(SubmissionService.class).SplitDocuments(largeDoc);
                        //  documentDc.getMutableItems().remove(largeDoc);
                        if (docArryList.size() > 1) {


                            documentDc.getMutableItems().remove(largeDoc);
                            dataManager.remove(largeDoc.getFileDescriptor());
                            try {
                                fileLoader.removeFile(largeDoc.getFileDescriptor());
                            }catch(Exception e){
                                System.out.println("Exception Occured while removing the file");
                            }

                            dataManager.remove(largeDoc);


                            Iterator<Document> doclistit = docArryList.iterator();
                            CommitContext docCommitCntx = new CommitContext();
                            while (doclistit.hasNext()) {
                                Document docRet = doclistit.next();
                                Document docCurr = null;
                                docCurr = dataManager.create(Document.class);

                                docCurr.setFileDescriptor(docRet.getFileDescriptor());
                                System.out.println("After Split file size is" + docRet.getFileDescriptor().getSize());
                                docCurr.setComments(docRet.getComments());
                                docCurr.setSplitNumber(docRet.getSplitNumber());
                                docCurr.setTitle(docRet.getTitle());
                                docCurr.setFilename(docRet.getFilename());
                                docCurr.setSubmissionID(sub);
                                docCurr.setAttachmentControlNumber(docRet.getAttachmentControlNumber());
                                docCurr.setLanguage(docRet.getLanguage());
                                // dataManager.u
                                documentDc.getMutableItems().add(docCurr);
                                docCommitCntx.addInstanceToCommit(docCurr);


                                documentSetTable.setSelected(docCurr);
                            }
                            dataManager.commit(docCommitCntx);
                            System.out.println("documentdc size is" + documentDc.getMutableItems().size());
                            System.out.println("documentdc size is" + documentDc.getItems().size());

                            //  sub.setDocument(documentDc.getItems());
                        }
                    }

                }
               }
        }
        submissionDc.getItem().getDocument().sort(Comparator.comparing(Document::getSplitNumber));
        int min_split =1,highest_split = 1;
        Iterator<Document> documentIt = submissionDc.getItem().getDocument().iterator();
        Dictionary<Integer,ArrayList<Document>> splitDocDict = new Hashtable<>();
        Long threshold = 75000000L;
        Dictionary<Integer,Long> splitSize = new Hashtable<>();
        List <HIHConfiguration> hihConfigList = dataManager.load(HIHConfiguration.class)
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
                if(dtTemp.getFileDescriptor() == null){
                    notifications.create().withCaption("please upload document").withType(Notifications.NotificationType.HUMANIZED).show();
                    btnsubmit.setVisible(true);
                    btnsubmit.setEnabled(true);
                    return;
                }
                currSize = currSize + dtTemp.getFileDescriptor().getSize();
                if(currSize > threshold){
                    System.out.println("Split size is greater than threshold");
                    notifications.create().withCaption("Split size is greater than threshold please correct them and submit").withType(Notifications.NotificationType.HUMANIZED).show();
                    btnsubmit.setVisible(true);
                    btnsubmit.setEnabled(true);
                    return;
                }
                splitSize.put(currSplitNum,currSize);
                docArryList.add(dtTemp);
                splitDocDict.put(currSplitNum,docArryList);
            }else{
                Long size;
                ArrayList<Document> docArryList = new ArrayList<>();
                if(dtTemp.getFileDescriptor() == null){
                    notifications.create().withCaption("please upload document").withType(Notifications.NotificationType.HUMANIZED).show();
                    btnsubmit.setVisible(true);
                    btnsubmit.setEnabled(true);
                    return;
                }
                size = dtTemp.getFileDescriptor().getSize();
                if(size > threshold){
                    System.out.println("Split size is greater than threshold");
                    notifications.create().withCaption("Split size is greater than threshold please correct them and submit").withType(Notifications.NotificationType.HUMANIZED).show();
                    btnsubmit.setVisible(true);
                    btnsubmit.setEnabled(true);
                    return;
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
      //  List<StatusChange> statlist = new ArrayList<>();
      //  sub.setStatusChange(statlist);
      //  sub.setDocument(documentDc.getItems());
      //  List<Error> errorlist = new ArrayList<>();
      //  sub.setError(errorlist);
       // sub.setUniqueIdList(UUID.randomUUID().toString());
     //   subSrc = AppBeans.get(SubmissionService.class);
      /*  if(subSrc == null){
            System.out.println("Submission Service Object is Null");
            return;
        }*/
        int numofSplits = ((Hashtable<Integer, ArrayList<Document>>) splitDocDict).keySet().size();
        if((min_split != 1) || (highest_split != numofSplits)){
            System.out.println("Splits entered are incorrect please correct them");
            notifications.create().withCaption("Splits entered are incorrect please correct them").withType(Notifications.NotificationType.HUMANIZED).show();
            btnsubmit.setVisible(true);
            btnsubmit.setEnabled(true);
            return;
        }
        String Splitstr = "1-"+numofSplits;
        Submission temp = AppBeans.get(SubmissionService.class).CallConnectApi(submissionDc.getItem(),Splitstr,null,splitDocDict.get(min_split));
        CommitContext commitContext = new CommitContext();
        submissionDc.getItem().setUniqueIdList(temp.getUniqueIdList());
        submissionDc.getItem().setLastSubmittedSplit(min_split);
        submissionDc.getItem().setHighestSpiltNo(highest_split);
        stageField.setValue(txt);
       // Submission temp = sub;
        if(temp.getStatusChange().size() > 0){

            System.out.println("Adding Status change items");
            Iterator<StatusChange> statchngit = temp.getStatusChange().iterator();
            while(statchngit.hasNext()) {
                StatusChange stat = statchngit.next();
                StatusChange stchng = dataManager.create(StatusChange.class);
                stchng.setSubmissionId(sub);
                stchng.setStatus(stat.getStatus());
                stchng.setResult(stat.getResult());
                stchng.setSplitInformation(stat.getSplitInformation());
                System.out.println(stat.getStatus());
                System.out.println(stat.getResult());
                statusChangeDc.getMutableItems().add(stchng);
                commitContext.addInstanceToCommit(stchng);


                statusChangesTable.setSelected(stchng);
            }
            System.out.println("get items size"+statusChangeDc.getItems().size());
           // sub.setStatusChange(statusChangeDc.getItems());

        }else{
            System.out.println("Error Occured While Submitting");
        }

        if(temp.getError().size() > 0){
            Iterator<Error> errIt = temp.getError().iterator();
            while(errIt.hasNext()){
                Error staterr = errIt.next();
                Error errTemp = dataManager.create(Error.class);
                errTemp.setSubmissionId(sub);
                errTemp.setSeverity(staterr.getSeverity());
                errTemp.setCodeContext(staterr.getCodeContext());
                errTemp.setErrorCode(staterr.getErrorCode());
                errTemp.setError(staterr.getError());
                errorDc.getMutableItems().add(errTemp);

                errorTable.setSelected(errTemp);
                commitContext.addInstanceToCommit(errTemp);
                submissionDc.getItem().setStatus("Error");


            }

        }else{
            System.out.println("Error Table is Empty");
            submissionDc.getItem().setStatus("Success");
        }
       // submissionDc.setItem(sub);
        if((temp.getStatusChange().size() != 0)  || (temp.getError().size() != 0)){
            System.out.println("Entered into get status change");
            Iterator<Document> documentIterator = documentDc.getItems().iterator();
            while(documentIterator.hasNext()){
                Document dtTemptoRem = documentIterator.next();
                if(dtTemptoRem.getSplitNumber() == 1){
                    dataManager.remove(dtTemptoRem.getFileDescriptor());
                    try {
                       // dtTemptoRem.getFileDescriptor().
                       // fileLoader.
                        fileLoader.removeFile(dtTemptoRem.getFileDescriptor());
                    }catch(Exception e){
                        System.out.println("Exception Occured while removing the file");
                    }

                    dtTemptoRem.setFileDescriptor(null);
                    documentDc.replaceItem(dtTemptoRem);
                   // commitContext.addInstanceToCommit(dtTemptoRem);
                }
            }
        }
        System.out.println("doc list size"+submissionDc.getItem().getDocument().size());
        commitContext.addInstanceToCommit(submissionDc.getItem());
     //   commitContext.addInstanceToCommit(sub);
     //   dataCnxt.remove(dcTemp);
      //  dataCnxt.merge(sub);
      //  dataManager.commit(commitContext);
       //   dataCnxt.commit();
       // dataCnxt.merge(submissionDc.getItem());
        dataManager.commit(commitContext);
        if((temp.getStatusChange().size() == 0) && (temp.getError().size() == 0)){
            submissionDc.getItem().setStatus("Error");
            notifications.create().withCaption("Submission Failed").withType(Notifications.NotificationType.HUMANIZED).show();

        }else{
            notifications.create().withCaption("Submission Completed check the status table for Update").withType(Notifications.NotificationType.HUMANIZED).show();
        }

    }

  /* @Subscribe(id = "statusChangeDc", target = Target.DATA_CONTAINER)
   private void onstatusChangeDcCollectionChange(
          CollectionContainer.CollectionChangeEvent<StatusChange> event) {
       CollectionChangeType changeType = event.getChangeType();
        System.out.println("item change event"+changeType.toString());



        // ...
    }*/
  @Subscribe("purposeOfSubmissionField")
  protected void onpurposeOfSubmissionFieldValueChange(HasValue.ValueChangeEvent<LineofBusiness> event) {
    LineofBusiness selval = event.getValue();
    if(selval != null ) {
        if (selval.getIsCaseIdDisplayed()) {
            esmdCaseIdField.setVisible(true);
        } else {
            esmdCaseIdField.setVisible(false);
        }
        if (selval.getIsEsmdClaimDisplayed()) {
            esMDClaimIDField.setVisible(true);
        } else {
            esMDClaimIDField.setVisible(false);
        }

        if (selval.getIsX12Supported()) {
            bSendinX12Field.setVisible(true);
        } else {
            bSendinX12Field.setVisible(false);
        }
    }
  }



    @EventListener
    public void onUiNotificationEvent(UiNotificationEvent event) {

        System.out.println("Received  event id is "+ event.getMessage());
        Long recvdId = Long.parseLong(event.getMessage());
        System.out.println("After parsing id is"+recvdId);
        System.out.println("Current form id is"+submissionDc.getItem().getId());
        Submission currItem = submissionDc.getItem();


        if(recvdId.compareTo(submissionDc.getItem().getId()) == 0) {





            Submission sub = dataManager.reload(submissionDc.getItem(), "submission-view");
            submissionDl.setEntityId(sub);
            //submissionDl.load();
             System.out.println("Submission string is "+submissionDl.getEntityId().toString());
             System.out.println("get status change size"+sub.getStatusChange().size());
            submissionDc.setItem(sub);
            System.out.println("get status change size"+statusChangeDc.getItems().size());

            if(currItem.getDocument().size() < sub.getDocument().size()){
                System.out.println("Setting Items");
                documentSetTable.setItems(new ContainerTableItems<>(documentDc) );
                //statusChangesTable.setSelected(sub.getStatusChange());
            }
           if(currItem.getStatusChange().size() < sub.getStatusChange().size()){
               System.out.println("Setting Items");
               statusChangesTable.setItems(new ContainerTableItems<>(statusChangeDc) );
               //statusChangesTable.setSelected(sub.getStatusChange());
           }
            if(currItem.getError().size() < sub.getError().size()){
                errorTable.setItems(new ContainerTableItems<>(errorDc) );
               // errorTable.setSelected(sub.getError());
               // errorTable.setItems();
            }
            if(currItem.getNotificationSlot().size() < sub.getNotificationSlot().size()){

                notificationSlotsTable.setItems(new ContainerTableItems<>(notificationSlotDc) );
               // notificationSlotsTable.setSelected(sub.getNotificationSlot());
            }
            if(!currItem.getStage().equals(sub.getStage())){
                notifications.create().withCaption("Notification Received").withType(Notifications.NotificationType.HUMANIZED).show();
            }
            //submissionDc.getView().
            System.out.println("updated value "+sub.getStage());
        }else{
            System.out.println("current item is null in uinotification event");
        }
    }

    @Subscribe("stageField")
    protected void onTextFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        System.out.println("Value change event for stage text field");
        if(event.getValue().equals("Draft")){
            btnsubmit.setVisible(true);
        }else{
            btnsubmit.setVisible(false);
        }

    }



   /* @Subscribe(id = "submissionDc", target = Target.DATA_CONTAINER)
    private void onsubmissionDcItemPropertyChange(
            InstanceContainer.ItemPropertyChangeEvent<Submission> event) {
        Submission customer = event.getItem();
        String changedProperty = event.getProperty();
        System.out.println("changed property name is "+changedProperty);
        Object currentValue = event.getValue();
        Object previousValue = event.getPrevValue();
        // ...
    }*/
 /*  @Install(to = "submissionDl", target = Target.DATA_LOADER)
   protected Submission submissionDlLoadDelegate(LoadContext<Submission> loadContext) {
       return dataManager.load(loadContext);
   }*/




    }
