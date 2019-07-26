package com.mettles.rioc.web.providers;

import com.mettles.rioc.UiNotificationEvent;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.entity.Error;
import com.mettles.rioc.service.SubmissionService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstancePropertyContainer;
import com.haulmont.cuba.gui.screen.*;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.Iterator;

@UiController("rioc_providers.edit")
@UiDescriptor("providers-edit.xml")
@EditedEntityContainer("providersDc")
@LoadDataBeforeShow
@SuppressWarnings("unchecked")
public class ProvidersEdit extends StandardEditor<Providers> {

    @Inject
    private DataManager dataManager;
    @Inject
    private InstancePropertyContainer<Submission> submissionDc;
    @Inject
    private InstanceContainer<Providers> providersDc;

    @Inject
    private LookupField<ProvLastSubmittedTranxType> TransTypeField;
    @Inject
    Table statusChangesTable;
    @Inject
    Table errorTable;
    @Inject
    Table notificationSlotsTable;
    @Inject
    CheckBox registered_for_emdrField;
    @Inject
    CollectionPropertyContainer<StatusChange> statusChangeDc;
    @Inject
    CollectionPropertyContainer<Error> errorDc;
    @Inject
    CollectionPropertyContainer<Error> notificationSlotDc;
    @Inject
    private Notifications notifications;
    @Inject
    private Button  btnsubmit;


    public void onBtnsubmitClick() {
        boolean bCurrItemNull = false;
        try {
            if (submissionDc.getItem() == null) {
                System.out.println("Submission is null");
            } else {
                System.out.println("Submission is not null");
            }
        }catch(Exception e){
            bCurrItemNull = true;
            System.out.println("Exception occured");
        }
        Submission currObj;
        Providers prov = providersDc.getItem();
        if(bCurrItemNull) {
             currObj = dataManager.create(Submission.class);
            currObj.setAuthorNPI("1234567890");
            currObj.setTitle("Emdr Registration");
            String Recepient_oid = "2.16.840.1.113883.3.6037.2.47";
            Recepient intdRecp = dataManager.loadValue(
                    "select o from rioc_Recepient o " +
                            "where o.oid = :recep ", Recepient.class)
                    .parameter("recep", Recepient_oid)
                    .one();
            if (intdRecp == null) {
                //Show error
                return;
            }
            LineofBusiness purpOfSub = null;
            String contentType = "5";
            purpOfSub = dataManager.loadValue(
                    "select o from rioc_LineofBusiness o " +
                            "where o.contentType = :purp ", LineofBusiness.class)
                    .parameter("purp", contentType)
                    .one();

            if (purpOfSub == null) {
                //Show error
                return;
            }

            currObj.setIntendedRecepient(intdRecp);
            currObj.setPurposeOfSubmission(purpOfSub);
            //currObj
            submissionDc.setItem(currObj);
            providersDc.getItem().setSubmissionID(currObj);

            currObj.setLastSubmittedSplit(1);
            currObj.setHighestSpiltNo(1);
            dataManager.commit(currObj);
        }else {
            currObj = submissionDc.getItem();
            providersDc.getItem().setSubmissionID(currObj);
            }

        boolean bRegister = false;

        if(currObj.getStage().equals("Draft")){
            if (TransTypeField.getValue() == ProvLastSubmittedTranxType.register) {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                bRegister = true;
            } else {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                bRegister = false;
            }
        }else if(currObj.getStage().equals("esMD - Request Accepted")){
            if(prov.getRegistered_for_emdr()){

                if (TransTypeField.getValue() == ProvLastSubmittedTranxType.register) {
                    notifications.create().withCaption("Provider already Registered").withType(Notifications.NotificationType.HUMANIZED).show();
                    return ;
                }else{
                    bRegister = false;
                    prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                }

            }else {

                if (TransTypeField.getValue() == ProvLastSubmittedTranxType.unregister) {
                    notifications.create().withCaption("Provider already DeRegistered").withType(Notifications.NotificationType.HUMANIZED).show();
                    return;
                }else{
                    bRegister = true;
                    prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                }

            }

        }else{
            if (TransTypeField.getValue() == ProvLastSubmittedTranxType.register) {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.register);
                bRegister = true;
            } else {
                prov.setLast_submitted_transaction(ProvLastSubmittedTranxType.unregister);
                bRegister = false;
            }

        }

            System.out.println("IS checked"+bRegister);
            currObj.setStage("Draft");

            Submission temp = AppBeans.get(SubmissionService.class).SubmiteMDRRegFile(prov, bRegister);

            if(temp.getError() != null){
                if(temp.getError().size() > 0) {
                    System.out.println("Error occured while submitting");
                }

            }else{
                if (TransTypeField.getValue() == ProvLastSubmittedTranxType.register)
                    prov.setRegistered_for_emdr(true);
                else
                    prov.setRegistered_for_emdr(false);
            }
            dataManager.commit(prov);
            currObj.setUniqueIdList(temp.getUniqueIdList());
          //  dataManager.commit(currObj);
            CommitContext commitContext = new CommitContext();


            if (temp.getStatusChange().size() > 0) {

                System.out.println("Adding Status change items");
                Iterator<StatusChange> statchngit = temp.getStatusChange().iterator();
                Iterator<StatusChange> statchngDcit = null;
                boolean bStatChngDC = false;
                if(statusChangeDc.getItems().size() > 0)
                {
                    bStatChngDC = true;
                }
                while (statchngit.hasNext()) {
                    StatusChange stat = statchngit.next();
                    boolean bItemFound = false;
                  /*  if(bStatChngDC){
                        statchngDcit = statusChangeDc.getItems().iterator();
                       while(statchngDcit.hasNext()){
                            StatusChange stTemp = statchngDcit.next();
                            if(stTemp)
                            if(stTemp.getStatus().equals(stat.getStatus()) && (stTemp.getCreateTs().compareTo(stat.getCreateTs()) == 0)){
                                bItemFound = true;
                                break;
                            }
                        }
                    }
                     if(bItemFound)
                         continue;*/
                    if(stat.getCreateTs() != null)
                        continue;

                    StatusChange stchng = dataManager.create(StatusChange.class);
                    stchng.setSubmissionId(currObj);
                    stchng.setStatus(stat.getStatus());
                    stchng.setResult(stat.getResult());
                    stchng.setSplitInformation(stat.getSplitInformation());
                    System.out.println(stat.getStatus());
                    System.out.println(stat.getResult());
                    statusChangeDc.getMutableItems().add(stchng);
                    commitContext.addInstanceToCommit(stchng);


                    statusChangesTable.setSelected(stchng);
                }
                System.out.println("get items size" + statusChangeDc.getItems().size());
                // sub.setStatusChange(statusChangeDc.getItems());

            } else {
                System.out.println("Error Occured While Submitting");
            }
            if(temp.getError() != null) {
                if (temp.getError().size() > 0) {
                    Iterator<Error> errIt = temp.getError().iterator();
                    Iterator<Error> errDcIt = null;
                    boolean bErrorDC = false;
                    if(errorDc.getItems().size() > 0)
                    {
                        bErrorDC = true;
                    }
                    while (errIt.hasNext()) {
                        Error staterr = errIt.next();
                      /*  boolean bItemFound = false;
                        if(bErrorDC){
                            errDcIt = errorDc.getItems().iterator();
                            while(errDcIt.hasNext()){
                                Error stTemp = errDcIt.next();
                                if(stTemp.getDescription().equals(staterr.getDescription()) && (stTemp.getCreateTs().compareTo(staterr.getCreateTs()) == 0)){
                                    bItemFound = true;
                                    break;
                                }
                            }
                        }
                        if(bItemFound)
                            continue;*/

                        if(staterr.getCreateTs() != null)
                            continue;

                        Error errTemp = dataManager.create(Error.class);
                        errTemp.setSubmissionId(currObj);
                        errTemp.setSeverity(staterr.getSeverity());
                        errTemp.setCodeContext(staterr.getCodeContext());
                        errTemp.setErrorCode(staterr.getErrorCode());
                        errTemp.setError(staterr.getError());
                        errorDc.getMutableItems().add(errTemp);

                        errorTable.setSelected(errTemp);
                        commitContext.addInstanceToCommit(errTemp);


                    }

                } else {
                    System.out.println("Error Table is Empty");
                }
            }
         //  commitContext.addInstanceToCommit(submissionDc.getItem());
          //  providersDc.getItem().setSubmissionID(currObj);
          //  commitContext.addInstanceToCommit(providersDc.getItem());
            dataManager.commit(commitContext);
            LoadContext<Submission> loadContext = LoadContext.create(Submission.class)
                    .setId(currObj.getId()).setView("submission-view");
            Submission subTemp =  dataManager.load(loadContext);
            subTemp.setUniqueIdList(temp.getUniqueIdList());
            dataManager.commit(subTemp);
            providersDc.getItem().setSubmissionID(subTemp);
            // dataManager.commit(providersDc.getItem());
    }

  /*  @Subscribe
    protected void onInit(InitEvent event) {
        registered_for_emdrField.addValueChangeListener(valueChangeEvent -> {
            if (Boolean.TRUE.equals(valueChangeEvent.getValue())) {
                btnsubmit.setCaption("Submit DeRegistration");
            } else {
                btnsubmit.setCaption("Submit Registration");
            }
        });
    }*/

    @EventListener
    public void onUiNotificationEvent(UiNotificationEvent event) {

        Long recvdId = Long.parseLong(event.getMessage());
        System.out.println("After parsing id is"+recvdId);
        System.out.println("Current form id is"+submissionDc.getItem().getId());
        Submission currItem = submissionDc.getItem();
        if(recvdId.compareTo(submissionDc.getItem().getId()) == 0) {





            Submission sub = dataManager.reload(submissionDc.getItem(), "submission-view");


            System.out.println("get status change size"+sub.getStatusChange().size());
            submissionDc.setItem(sub);
            System.out.println("get status change size"+statusChangeDc.getItems().size());

             if((currItem.getStatusChange() != null) && (sub.getStatusChange() != null)) {
                 if (currItem.getStatusChange().size() < sub.getStatusChange().size()) {
                     System.out.println("Setting Items");
                     statusChangesTable.setItems(new ContainerTableItems<>(statusChangeDc));
                     //statusChangesTable.setSelected(sub.getStatusChange());
                 }
             }
             if( (currItem.getError() != null) && (sub.getError() != null)) {
                 if (currItem.getError().size() < sub.getError().size()) {
                     errorTable.setItems(new ContainerTableItems<>(errorDc));
                     // errorTable.setSelected(sub.getError());
                     // errorTable.setItems();
                 }
             }
             if((currItem.getNotificationSlot() != null)&&(sub.getNotificationSlot() != null)) {
                 if (currItem.getNotificationSlot().size() < sub.getNotificationSlot().size()) {

                     notificationSlotsTable.setItems(new ContainerTableItems<>(notificationSlotDc));
                     // notificationSlotsTable.setSelected(sub.getNotificationSlot());
                 }
             }
            if(!currItem.getStage().equals(sub.getStage())){
                notifications.create().withCaption("Notification Received").withType(Notifications.NotificationType.HUMANIZED).show();
            }
            System.out.println("doc stage"+sub.getStage()+"trans type"+TransTypeField.getValue());

            if(sub.getStage().equals("esMD - Request Accepted") &&  TransTypeField.getValue() == ProvLastSubmittedTranxType.register)
            {
                System.out.println("setting boolean flag");
                providersDc.getItem().setRegistered_for_emdr(true);
                registered_for_emdrField.setValue(true);
               // dataManager.commit(providersDc.getItem());
            }
            else if(sub.getStage().equals("esMD - Request Accepted") &&  TransTypeField.getValue() == ProvLastSubmittedTranxType.unregister)
            {
                providersDc.getItem().setRegistered_for_emdr(false);
                registered_for_emdrField.setValue(false);
              //  dataManager.commit(providersDc.getItem());

            }

                //submissionDc.getView().
            System.out.println("updated value "+sub.getStage());
        }else{
            System.out.println("current item is null in uinotification event");
        }

    }
}