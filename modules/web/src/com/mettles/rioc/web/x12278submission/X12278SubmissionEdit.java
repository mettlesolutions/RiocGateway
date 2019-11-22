package com.mettles.rioc.web.x12278submission;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import org.apache.poi.hssf.OldExcelFormatException;
import org.springframework.context.event.EventListener;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.mettles.rioc.*;
import com.mettles.rioc.entity.*;
import com.mettles.rioc.service.SubmissionService;
//import com.mettles.esMDConnectLib.*;
//import com.mettles.rioc.x12.EDIWriteStatus;
//import com.mettles.rioc.x12.ElemLvlErrInfo;
//import com.mettles.rioc.x12.SegLvlErrInfo;
//import com.mettles.rioc.x12.EDIWriteStatus;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@UiController("rioc_X12278Submission.edit")
@UiDescriptor("x12278-submission-edit.xml")
@EditedEntityContainer("x12278SubmissionDc")
@LoadDataBeforeShow
public class X12278SubmissionEdit extends StandardEditor<X12278Submission> {


    @Inject
    Button btnparse;
    @Inject
    Button btnsubmit;
    @Inject
    Table paErrorTable;
    @Inject
    Table paStatusChangeTable;
    @Inject
    Table paNotificationSlotTable;
    @Inject
    Table paDocumentTable;


    @Inject
    TextField esmdTransactionIdField;

    @Inject
    TextField suppDocesmdTransactionIdField;

    @Inject
    TextField suppDocMessageField;

    @Inject
    TextField statusField;

    @Inject
    private FileUploadField uploadFileField;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private Notifications notifications;

    @Inject
    private DataManager dataManager;

    private Hashtable<String, String> inputKey = new Hashtable<String, String>();
    private Hashtable<String, String> err999Key = new Hashtable<String, String>();

    private String EDI;

    private File file = null;
    private FileDescriptor fd = null;
    //private String fileName = null;

    @Inject
    InstanceLoader<X12278Submission> x12278SubmissionsDl;

    @Inject
    InstanceContainer<X12278Submission> x12278SubmissionDc;

    @Inject
    CollectionPropertyContainer<PADocument> paDocumentDc;

    @Inject
    CollectionPropertyContainer<PAError> paErrorDc;
    @Inject
    CollectionPropertyContainer<PAStatusChange> paStatusChangeDc;
    @Inject
    CollectionPropertyContainer<PANotificationSlot> paNotificationSlotDc;


    private String requesterNPI="";

    X12278Submission x12sub;
    //EDIWriteStatus ediWrtStats = new EDIWriteStatus();
    //EDIWriteStatus retVal = new EDIWriteStatus();


    @Subscribe
    protected void onInit(InitEvent event)  {
        //btnsubmit.setEnabled(false);
        uploadFileField.addAfterValueClearListener(valueClearEvent -> {
            System.out.println("On Press Clear Button");
            btnsubmit.setEnabled(false);
            btnparse.setEnabled(false);
            System.out.println("File ID: "+uploadFileField.getFileId());
            System.out.println("File Exists: "+file.exists());
            //System.out.println("File Delete: "+file.delete());
            if(file.exists()){
                try {
                    file.delete();
                    System.out.println("Inside File Delete: ");
                    System.out.println("------------------------");
                    System.out.println("File Exists: "+file.exists());
                    fd=null;
                    requesterNPI = null;
                    EDI = null;
                    //h = null;
                    inputKey.clear();
                    //err999Key = null;
                    err999Key.clear();
                    //x12sub.setX12UnqID(null);
                    x12sub.setAttachmentControlNumber(null);
                    System.out.println("fd: "+fd+", requesterNPI: "+requesterNPI+", EDI: "+EDI+", inputKey: "+inputKey+", err999Key: "+err999Key);
                    //System.out.println("X12 Unique ID: "+x12sub.getX12UnqID());
                    System.out.println("Attachment Control Number: "+x12sub.getAttachmentControlNumber());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        });
        uploadFileField.addFileUploadSucceedListener(uploadSucceedEvent -> {
            System.out.println("File ID: "+uploadFileField.getFileId());
            file = fileUploadingAPI.getFile(uploadFileField.getFileId());
            //fileName = uploadFileField.getFileName();
            fd = uploadFileField.getFileDescriptor();
            System.out.println("File Length: "+file.length());

            if (file.length() != 0) {
                //notifications.create().withCaption("Loading File: "+ fileName+"  !!!!").show();
                System.out.println("File Name: "+file.getName());
                btnparse.setEnabled(true);
                btnparse.setVisible(true);
                System.out.println("File Loaded: "+fd.getName()+" Press Parse Button");
                notifications.create().withCaption("File Loaded: "+fd.getName()+" Press Parse Button").show();
                //notifications.create().withCaption("File Loaded: "+ fileName+" Press Parse Button").show();
            }
            else
            {
                System.out.println("Uploaded File: "+fd.getName()+" is Empty, Kindly try Again");
                notifications.create().withCaption("Uploaded File: "+fd.getName()+" is Empty, Kindly try Again").show();
                //notifications.create().withCaption("Uploaded File: "+fileName+" is Empty, Kindly try Again").show();
            }

        });

        uploadFileField.addFileUploadErrorListener(uploadErrorEvent ->
                notifications.create()
                        .withCaption("File upload error, Kindly try Again")
                        .show());
    }


    public void onBtnparseClick() {
        x12sub = x12278SubmissionDc.getItem();
        //this.commitChanges();
        try{
            if(x12sub.getIntendedRecepient().getIntendedRecepient()== null || x12sub.getIntendedRecepient().getIntendedRecepient().isEmpty()) {
                notifications.create().withCaption("Intended Recepient Should not be Empty, Please Select Recepient and Upload file").show();
            }
            else{
                try {
                    System.out.println("EDI ID: "+x12sub.getIntendedRecepient().getEdiID());
                    String iRecEDI = x12sub.getIntendedRecepient().getEdiID();
                    if (file.length() != 0 && iRecEDI != null) {
                        //Checking for EXCEL Spreedsheet file only
                        try {
                            System.out.println("Excel File Extension: "+fd.getExtension());
                            System.out.println("File Name using file Descriptor: "+fd.getName());
                            if(fd.getExtension().matches("xlsx|xls")) {
                            //if (FilenameUtils.getExtension(fileName).matches("xlsx|xls")) {
                                System.out.println("File Name: "+file.getName());
                                try{
                                    //System.out.println("Hash Table Size: "+h.size());
                                    inputKey = createHashTable(file);
                                    System.out.println("Input Hash Table: "+inputKey);
                                    //Checking EXCEL File is Empty or Incorrect
                                    if(inputKey== null || inputKey.isEmpty()){
                                        System.out.println("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Reupload");
                                        notifications.create().withCaption("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Re-upload").show();
                                    }
                                    else if(inputKey.size() > 0){
                                        requesterNPI = inputKey.get("RequesterRequesterNPI");
                                        if (requesterNPI == null || requesterNPI.isEmpty()) {
                                            notifications.create().withCaption("RequesterRequesterNPI Field is empty, Missing Field in the Excel File").show();
                                        } else {
                                            try{

                                                System.out.println("Requested requesterNPI: "+requesterNPI);
                                                //UUID uuid = UUID.randomUUID();
                                                //x12sub.setX12UnqID(uuid.toString());
                                                //System.out.println("X12278 Submission Unique ID: "+x12sub.getX12UnqID());

                                                //Calling Service for Parsing the HashTable
                                                X12278ValidationStatus x12278validstus = AppBeans.get(SubmissionService.class).ParseHashtable(inputKey, iRecEDI, requesterNPI);
                                                System.out.println("Parse Validation Status of Uploaded Document:");
                                                System.out.println("------------------------------------------------");
                                                System.out.println("Validation Status: " + x12278validstus.getStatus());
                                                System.out.println("Validation Status Code: " + x12278validstus.getStatusCode());
                                                System.out.println("Validation Error Message: " + x12278validstus.getErrorMessage());

                                                if(x12278validstus.getStatusCode() == 0  && x12278validstus.getStatus().equalsIgnoreCase("Success"))
                                                {
                                                    System.out.println("Validation Error 999 HashTable: "+x12278validstus.getError999Key());
                                                    System.out.println("EDI String: "+x12278validstus.getEDI());
                                                    System.out.println("PWK: "+x12278validstus.getPwk());
                                                    err999Key = x12278validstus.getError999Key();
                                                    EDI = x12278validstus.getEDI();
                                                    x12sub.setAttachmentControlNumber(x12278validstus.getPwk());
                                                    System.out.println("Document Attachment Control Number: "+x12sub.getAttachmentControlNumber());
                                                    if (EDI != null && !EDI.isEmpty()) {
                                                        String status = x12278validstus.getStatus();
                                                        //uploadFileField.setEnabled(false);
                                                        btnsubmit.setEnabled(true);
                                                        //btnsubmit.setVisible(true);
                                                        notifications.create().withCaption("Parsed " + status + "fully, Now Press Submit Button for Document Submission").show();
                                                    }
                                                }
                                                else if(x12278validstus.getStatusCode() == 1)
                                                {
                                                    notifications.create().withCaption(" "+x12278validstus.getErrorMessage()).show();
                                                }
                                                else if(x12278validstus.getStatusCode() == 2)
                                                {
                                                    notifications.create().withCaption("Invalid EXCEL Document: "+x12278validstus.getStatus()).show();
                                                }
                                                else if(x12278validstus.getStatusCode() == 3)
                                                {
                                                    notifications.create().withCaption("Invalid EXCEL Document: "+x12278validstus.getStatus()).show();
                                                }
                                            }
                                            catch(Exception e)
                                            {
                                                e.printStackTrace();
                                                notifications.create().withCaption("Invalid FILE, Mandatory Fields are NULL, Please Try to Upload Correct Document").show();

                                            }
                                        }
                                    }

                                }
                                //Checking EXCEL File is Empty or Incorrect
                                catch(NullPointerException e)
                                {
                                    e.printStackTrace();
                                    System.out.println("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Reupload");
                                    notifications.create().withCaption("Uploaded Excel File is Empty or Incorrect, Kindly check Uploaded File and try Re-upload").show();
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                    notifications.create().withCaption("Server Error: Please contact Support Help").show();
                                }

                            } else {
                                notifications.create().withCaption("Invalid FILE, Please Upload EXCEL Document and Press PARSE Button").show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            notifications.create().withCaption("Server Error: Please contact Support Help").show();
                        }
                    } else {
                        notifications.create().withCaption("No EDI ID for Selected Intendend Recepient").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notifications.create().withCaption("Either File is empty or No EDI ID for Selected Intendend Recepient" +e.getMessage()).show();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            notifications.create().withCaption("Intended Recepient Should not be Empty, Please Select Recepient").show();
        }
    }


    //Creating HashTable from provided EXCEL ( .xlsx and .xls ) SpreedSheet File
    protected Hashtable<String, String> createHashTable(File file) {

        System.out.println("Inside HashTable");
        FileInputStream fileIn=null;
        Workbook workbook=null;
        boolean bError = false;
        try
        {
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
                    if(count == 0) {
                        key = trimmerVal;
                    }else {
                        value = trimmerVal;
                    }
                    count++;
                }
                if(!value.equals("")) {
                    inputKey.put(key, value);
                    System.out.println("key is"+key+"value is"+value);
                }
            }

            workbook.close();
            fileIn.close();

            System.out.println("End of Hash Table Creation");
        }

        catch(FileNotFoundException e)
        {
            bError = true;
            e.printStackTrace();
            notifications.create().withCaption(e.getMessage()).show();
        }

        catch(IOException e)
        {
            bError = true;
            e.printStackTrace();
            notifications.create().withCaption(e.getMessage()).show();
        }
        catch(NullPointerException e)
        {
            bError = true;
            e.printStackTrace();
            notifications.create().withCaption(e.getMessage()).show();
        }

        catch(Exception e)
        {
            bError = true;
            e.printStackTrace();
            System.out.println(e.getMessage());
            notifications.create().withCaption("Error: "+e.getMessage()).show();
        }
        if(bError)
        {
            System.out.println("After Exception:");
            System.out.println("-------------------");
            try{
                if (workbook != null) {
                    workbook.close();
                }

                if (fileIn != null || fileIn.available()>0) {
                    fileIn.close();
                }
                //System.out.println("FileIN Closed: "+fileIn.available());
            }
            catch (IOException e) {
                    e.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return inputKey;
    }

    public void onBtnsubmitClick() {

        UUID uuid = UUID.randomUUID();
        x12sub.setX12UnqID(uuid.toString());
        this.commitChanges();
        uploadFileField.setEnabled(false);
        //New Code to check Document is not Null

        if(x12sub.getPaDocument() != null && !x12sub.getPaDocument().isEmpty())
        {
            //x12278sub = x12278SubmissionDc.getItem();
            System.out.println("X12278Submission unqiue ID: "+x12sub.getX12UnqID());
            System.out.println("X12278Submission Attachment Control Number: "+x12sub.getAttachmentControlNumber());

            X12278SubmissionStatus x12278subStatus;
            try {

                if(EDI != null && !EDI.isEmpty())
                {
                    System.out.println(" On Submit Button  ");
                    System.out.println("EDI String is"+EDI);

                    x12278subStatus = AppBeans.get(SubmissionService.class).SubmitX12278Submission(x12sub, EDI, err999Key);
                    String status = x12278subStatus.getErrorMessage();
                    System.out.println("Status: " + status);
                    System.out.println("Error Code: "+x12278subStatus.getErrorCode());

                    CommitContext commitContext = new CommitContext();

                    if(status !=null && !status.isEmpty())
                    {
                        //ESMD Server Down and unavailable
                        if(x12278subStatus.getErrorCode().equalsIgnoreCase("-1")) {
                            try {
                                x12sub.setStatus("FAILED");

                                //CommitContext commitContext = new CommitContext();
                                PAError paErrTemp = dataManager.create(PAError.class);
                                System.out.println(" PA Error Trans Type: " + x12278subStatus.getTransType());
                                System.out.println(" PA Error Description: " + x12278subStatus.getErrorMessage());
                                System.out.println(" PA Error Code: " + x12278subStatus.getErrorCode());
                                paErrTemp.setX12278submissionID(x12sub);
                                paErrTemp.setTransType(x12278subStatus.getTransType());
                                paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                                paErrTemp.setErrorCode(x12278subStatus.getErrorCode());
                                commitContext.addInstanceToCommit(paErrTemp);
                                // Setting
                                paErrorDc.getMutableItems().add(paErrTemp);
                                paErrorTable.setSelected(paErrTemp);
                                //dataManager.commit(commitContext);

                                notifications.create().withCaption("Submission InComplete Server Error check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                //notifications.create().withCaption(status+" Contact Support").withType(Notifications.NotificationType.HUMANIZED).show();
                                commitContext.addInstanceToCommit(x12sub);

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                notifications.create().withCaption("Server Error: Please contact Support Help").show();
                            }
                        }

                        //In Case of ESMD Server Failure or In Case of HIH Configuration is Null
                        else if(x12278subStatus.getErrorCode().equalsIgnoreCase("Failure"))
                        {
                            try {
                                x12sub.setStatus("FAILED");

                                //CommitContext commitContext = new CommitContext();
                                PAError paErrTemp = dataManager.create(PAError.class);
                                System.out.println(" PA Error Trans Type: " + x12278subStatus.getTransType());
                                System.out.println(" PA Error Description: " + x12278subStatus.getErrorMessage());
                                System.out.println(" PA Error Code: " + x12278subStatus.getErrorCode());
                                paErrTemp.setX12278submissionID(x12sub);
                                paErrTemp.setTransType(x12278subStatus.getTransType());
                                paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                                paErrTemp.setCodeContext(x12278subStatus.getErrorCode());
                                commitContext.addInstanceToCommit(paErrTemp);
                                // Setting
                                paErrorDc.getMutableItems().add(paErrTemp);
                                paErrorTable.setSelected(paErrTemp);
                                //dataManager.commit(commitContext);

                                notifications.create().withCaption("Submission InComplete check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();
                                commitContext.addInstanceToCommit(x12sub);

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                notifications.create().withCaption("Server Error: Please contact Support Help").show();
                            }
                        }

                        // In Case OF Success Submission TO ESMD Connect x12278subStatus.getErrorCode().equalsIgnoreCase("Success")
                        else if (status.equalsIgnoreCase("No Error"))
                        {
                            try {
                                System.out.println(" In Success Submission to ESMD Connect");
                                System.out.println("------------------------------------------");
                                System.out.println(" Error Code: " + x12278subStatus.getErrorCode());
                                System.out.println("ESMD Transaction ID: " + x12278subStatus.getEsmdTransactionId());
                                x12sub.setEsmdTransactionId(x12278subStatus.getEsmdTransactionId());
                                x12278SubmissionDc.getItem().setEsmdTransactionId(x12278subStatus.getEsmdTransactionId());
                                //this.dataManager.commit(x12sub);

                                System.out.println("Insert into PA STATUS CHANGE DataBase for X12278 Submission PA Request-Response to ESMD");
                                System.out.println(" PA Status Change Trans Type: " + x12278subStatus.getTransType());

                                //CommitContext commitContext2 = new CommitContext();
                                PAStatusChange paStsChng = dataManager.create(PAStatusChange.class);
                                paStsChng.setX12278submissionID(x12sub);
                                //paStsChng.setTransType("X12 Transaction");
                                paStsChng.setTransType(x12278subStatus.getTransType());
                                paStsChng.setEsmdTransactionId(x12278subStatus.getEsmdTransactionId());
                                paStsChng.setResult("Success");
                                paStsChng.setStatus("PA Request-Response Success");

                                paStatusChangeDc.getMutableItems().add(paStsChng);
                                //commitContext2.addInstanceToCommit(paStsChng);
                                commitContext.addInstanceToCommit(paStsChng);
                                //dataManager.commit(commitContext2);
                                //this.dataManager.commit(commitContext1);
                                paStatusChangeTable.setSelected(paStsChng);

                                //x12sub.setStatus("PA Request-Response Success");

                                notifications.create().withCaption("PA Request-Response Completed check the PA Status Change table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                System.out.println("Segment Status Information Size: " + x12278subStatus.getSegStatus().size());
                                ArrayList<StatusInfo> paStslist = x12278subStatus.getSegStatus();

                            /* New Code using while loop

                            Iterator<StatusInfo> statsInfoIt = x12278subStatus.getSegstatus().iterator();

                            while(statsInfoIt.hasNext()) {
                                StatusInfo stsIn = statsInfoIt.next();
                                if(stsIn.getStatusCode().equalsIgnoreCase("2"))
                                {

                                }
                            }
                            //End of new code While Loop */

                                for (int x = 0; x < paStslist.size(); x++) {
                                    System.out.println("Transaction Type: " + paStslist.get(x).getTransType());
                                    System.out.println("PA Document set Submission Status Code: " + paStslist.get(x).getStatusCode());

                                    System.out.println("PA Document Unique ID: " + paStslist.get(x).getUniqueID());
                                    x12sub.setDocUnqID(paStslist.get(x).getUniqueID());
                                    System.out.println("X12278Submission Document Unique ID: " + x12sub.getDocUnqID());

                                    if (paStslist.get(x).getStatusCode().equalsIgnoreCase("2")) {
                                        System.out.println("ERROR While Document Submission with Error Code: 2");
                                        System.out.println("-----------------------------------------------------");
                                        System.out.println("Checking ERROR while Document Submission Segment Error Length: " + x12278subStatus.getSegErrs().size());

                                        x12sub.setStatus("FAILED");

                                        //setting data into PA ERROR Entity
                                        ArrayList<ErrorInfo> paErrlist = x12278subStatus.getSegErrs();

                                    /*New Code using while Loop
                                    if (x12278subStatus.getSegErrs() != null) {
                                        Iterator<ErrorInfo> errinfoIt = x12278subStatus.getSegErrs().iterator();

                                        while(errinfoIt.hasNext()) {
                                            ErrorInfo errIn = errinfoIt.next();
                                            System.out.println("Error Context: "+errIn.getErrorCtx());
                                            System.out.println("Error Code: "+errIn.getErrorCode());
                                            System.out.println("Error Description: "+errIn.getErrorDesp());
                                            System.out.println("Error Severity: "+errIn.getSeverity());
                                            System.out.println("Error TransType: "+errIn.getTransType());
                                            //CommitContext commitContext = new CommitContext();
                                            PAError paErrTemp = dataManager.create(PAError.class);
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
                                            // Setting
                                            paErrorDc.getMutableItems().add(paErrTemp);
                                            paErrorTable.setSelected(paErrTemp);
                                            //dataManager.commit(commitContext);
                                            //this.dataManager.commit(commitContext);

                                        }
                                    }
                                    // End of while Loop */

                                        for (int y = 0; y < paErrlist.size(); y++) {
                                            try {
                                                System.out.println("Error Context: " + paErrlist.get(y).getErrorCtx());
                                                System.out.println("Error Code: " + paErrlist.get(y).getErrorCode());
                                                System.out.println("Error Description: " + paErrlist.get(y).getErrorDesp());
                                                System.out.println("Error Severity: " + paErrlist.get(y).getSeverity());
                                                System.out.println("Error TransType: " + paErrlist.get(y).getTransType());
                                                //CommitContext commitContext = new CommitContext();
                                                PAError paErrTemp = dataManager.create(PAError.class);
                                                //System.out.println(" PA Error Trans TYpe: "+x12278subStatus.getTransType());
                                                paErrTemp.setX12278submissionID(x12sub);
                                                paErrTemp.setCodeContext(paErrlist.get(y).getErrorCtx());
                                                paErrTemp.setErrorCode(paErrlist.get(y).getErrorCode());
                                                paErrTemp.setDescription(paErrlist.get(y).getErrorDesp());
                                                paErrTemp.setSeverity(paErrlist.get(y).getSeverity());
                                                paErrTemp.setTransType(paErrlist.get(y).getTransType());
                                                //paErrTemp.setTransType(TransType.X12);

                                                //x12sub.setDocUnqID(paErrlist.get(y).getUniqueID());

                                                //commitContext2.addInstanceToCommit(paErrTemp);
                                                commitContext.addInstanceToCommit(paErrTemp);
                                                // Setting
                                                paErrorDc.getMutableItems().add(paErrTemp);
                                                paErrorTable.setSelected(paErrTemp);
                                                //dataManager.commit(commitContext);
                                                //this.dataManager.commit(commitContext);

                                                //x12sub.setSuppDocMessage(paErrlist.get(y).getErrorCtx());
                                                x12sub.setSuppDocMessage("FAILED");

                                            }
                                            catch(Exception e)
                                            {
                                                e.printStackTrace();
                                                notifications.create().withCaption("Server Error: Please contact Support Help").show();
                                            }

                                        }

                                        notifications.create().withCaption("Submission InComplete check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                    } else if (paStslist.get(x).getStatusCode().equalsIgnoreCase("1")) {
                                        System.out.println("ERROR While Document Submission with Error Code: 1");
                                        System.out.println("-----------------------------------------------------");

                                        try {
                                            x12sub.setStatus("FAILED");

                                            //CommitContext commitContext = new CommitContext();
                                            PAError paErrTemp = dataManager.create(PAError.class);
                                            System.out.println(" PA Error Trans TYpe: " + paStslist.get(x).getTransType());
                                            paErrTemp.setX12278submissionID(x12sub);
                                            paErrTemp.setCodeContext(paStslist.get(x).getResult());
                                            paErrTemp.setErrorCode(paStslist.get(x).getStatusCode());
                                            paErrTemp.setDescription(paStslist.get(x).getStatus());
                                            //paErrTemp.setSeverity();
                                            paErrTemp.setTransType(paStslist.get(x).getTransType());

                                            //x12sub.setDocUnqID(paStslist.get(x).getUniqueID());

                                            //commitContext2.addInstanceToCommit(paErrTemp);
                                            commitContext.addInstanceToCommit(paErrTemp);

                                            // Setting
                                            paErrorDc.getMutableItems().add(paErrTemp);
                                            paErrorTable.setSelected(paErrTemp);
                                            //dataManager.commit(commitContext);
                                            //this.dataManager.commit(commitContext);

                                            //x12sub.setSuppDocMessage(paStslist.get(x).getResult());
                                            x12sub.setSuppDocMessage("FAILED");

                                            notifications.create().withCaption("Submission InComplete check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            notifications.create().withCaption("Server Error: Please contact Support Help").show();
                                        }


                                    } else if (paStslist.get(x).getStatusCode().equalsIgnoreCase("0")) {

                                        try {
                                            x12sub.setStatus("SUCCESS");

                                            //CommitContext commitContext = new CommitContext();
                                            PAStatusChange paStChngTemp = dataManager.create(PAStatusChange.class);
                                            System.out.println(" PA Status Change Trans Type: " + paStslist.get(x).getTransType());
                                            System.out.println(" PA Status Change Result: " + paStslist.get(x).getResult());
                                            System.out.println(" PA Status Change Status: " + paStslist.get(x).getStatus());
                                            System.out.println(" Document Unique ID: " + paStslist.get(x).getUniqueID());
                                            paStChngTemp.setX12278submissionID(x12sub);
                                            paStChngTemp.setTransType(paStslist.get(x).getTransType());
                                            paStChngTemp.setResult(paStslist.get(x).getResult());
                                            paStChngTemp.setStatus(paStslist.get(x).getStatus());

                                            //x12sub.setDocUnqID(paStslist.get(x).getUniqueID());

                                            //commitContext2.addInstanceToCommit(paStChngTemp);
                                            commitContext.addInstanceToCommit(paStChngTemp);
                                            // Setting
                                            paStatusChangeDc.getMutableItems().add(paStChngTemp);
                                            paStatusChangeTable.setSelected(paStChngTemp);
                                            //dataManager.commit(commitContext);

                                            x12sub.setSuppDocMessage(paStslist.get(x).getResult());

                                            notifications.create().withCaption("Document Submission " + paStslist.get(x).getResult() + " check the PA Status Change table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            notifications.create().withCaption("Server Error: Please contact Support Help").show();
                                        }
                                    }
                                }
                                //dataManager.commit(commitContext2);
                                commitContext.addInstanceToCommit(x12sub);

                            }

                            catch(Exception e)
                            {
                                e.printStackTrace();
                                notifications.create().withCaption("Server Error: Please contact Support Help").show();
                            }
                        }


                        // In Case OF ERROR on 999 After Submission TO ESMD Connect
                        if(status.equalsIgnoreCase("Error on 999"))
                        {
                            try {
                                System.out.println("ERROR on 999 X12278 Submission Edit Logic");
                                System.out.println("--------------------------------------------");
                                System.out.println("Checking Parse on ERROR on 999 Segment Error Length: " + x12278subStatus.getSegErrs().size());

                                x12sub.setStatus("FAILED");
                                //this.dataManager.commit(x12sub);
                                //setting data into PA ERROR Entity
                                ArrayList<ErrorInfo> paErrlist = x12278subStatus.getSegErrs();

                            //New Code using While Loop
                            if (x12278subStatus.getSegErrs() != null)
                            {
                                Iterator<ErrorInfo> errinfoIt = x12278subStatus.getSegErrs().iterator();

                                while (errinfoIt.hasNext()) {

                                    try {
                                        //CommitContext commitContext = new CommitContext();
                                        PAError paErrTemp = dataManager.create(PAError.class);
                                        ErrorInfo errIn = errinfoIt.next();
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
                                        // Setting
                                        paErrorDc.getMutableItems().add(paErrTemp);
                                        paErrorTable.setSelected(paErrTemp);
                                        //dataManager.commit(commitContext);
                                        //this.dataManager.commit(commitContext);
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                        notifications.create().withCaption("Server Error: Please contact Support Help").show();
                                    }
                                }
                            }

                            /*End of New Code while loop

                                for (int x = 0; x < paErrlist.size(); x++) {
                                    //CommitContext commitContext = new CommitContext();
                                    PAError paErrTemp = dataManager.create(PAError.class);
                                    System.out.println("Error Context: " + paErrlist.get(x).getErrorCtx());
                                    System.out.println("Error Code: " + paErrlist.get(x).getErrorCode());
                                    System.out.println("Error Description: " + paErrlist.get(x).getErrorDesp());
                                    System.out.println(" PA Error Trans TYpe: " + x12278subStatus.getTransType());
                                    paErrTemp.setX12278submissionID(x12sub);
                                    paErrTemp.setTransType(x12278subStatus.getTransType());
                                    paErrTemp.setErrorCode(paErrlist.get(x).getErrorCode());
                                    paErrTemp.setDescription(paErrlist.get(x).getErrorDesp());
                                    paErrTemp.setCodeContext(paErrlist.get(x).getErrorCtx());
                                    commitContext.addInstanceToCommit(paErrTemp);
                                    // Setting
                                    paErrorDc.getMutableItems().add(paErrTemp);
                                    paErrorTable.setSelected(paErrTemp);
                                    //dataManager.commit(commitContext);
                                    //this.dataManager.commit(commitContext);

                                }
                                 End of old Code */

                                notifications.create().withCaption("Submission InComplete check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();

                                commitContext.addInstanceToCommit(x12sub);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                notifications.create().withCaption("Server Error: Please contact Support Help").show();
                            }
                        }

                        /*In Case of Internal Errors
                        if(status.equalsIgnoreCase("Internal Error"))
                        {
                            x12sub.setStatus("FAILED");

                            //CommitContext commitContext = new CommitContext();
                            PAError paErrTemp = dataManager.create(PAError.class);
                            System.out.println(" PA Error Trans Type: "+x12278subStatus.getTransType());
                            System.out.println(" PA Error Description: "+x12278subStatus.getErrorMessage());
                            System.out.println(" PA Error Context: "+x12278subStatus.getErrorCode());
                            paErrTemp.setX12278submissionID(x12sub);
                            paErrTemp.setTransType(x12278subStatus.getTransType());
                            paErrTemp.setDescription(x12278subStatus.getErrorMessage());
                            paErrTemp.setCodeContext(x12278subStatus.getErrorCode());
                            commitContext.addInstanceToCommit(paErrTemp);
                            // Setting
                            paErrorDc.getMutableItems().add(paErrTemp);
                            paErrorTable.setSelected(paErrTemp);
                            //dataManager.commit(commitContext);

                            notifications.create().withCaption("Submission InComplete check the PA Error table for Update").withType(Notifications.NotificationType.HUMANIZED).show();
                        }
                        */

                    }

                    //InCase of no Status i.e Server Down and not able to connect to ESMD Connect
                    else if(status == null || status.isEmpty()) {
                        x12sub.setStatus("Server Down");
                        System.out.println("status is null");
                        notifications.create().withCaption("Server Error: Please contact Support Help").show();
                        commitContext.addInstanceToCommit(x12sub);
                    }
                    try {
                        //dataManager.commit(x12sub);
                        dataManager.commit(commitContext);
                        //dataManager.commit(x12sub);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else if(EDI == null || EDI.isEmpty()){
                    System.out.println("EDI is null");
                    notifications.create().withCaption("EDI Not Generated, Server Error: Please contact Support Help").show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
                notifications.create().withCaption("Server Error: Please contact Support Help").show();
            }

        }

        else{
            notifications.create().withCaption("Please Upload Documents in Document Set and Try to ReSubmit").show();
        }


    }


    @EventListener
    public void onUiNotificationEvent1(UiNotificationEvent event) {
        try{

            System.out.println("Received  event id is " + event.getMessage());
            Long recvdId = Long.parseLong(event.getMessage());
            System.out.println("After parsing id is" + recvdId);
            System.out.println("Current form id is" + x12278SubmissionDc.getItem().getId());
            X12278Submission currItem = x12278SubmissionDc.getItem();

            System.out.println("Current X12278 Submission Supporting ESMD Transaction ID: " + currItem.getSuppDocesmdTransactionId());
            System.out.println("Current X12278 Submission Supporting Document Message: " + currItem.getSuppDocMessage());
            System.out.println("Current X12278 Submission PA Status: " + currItem.getStatus());

            System.out.println("----------------------------------------------------------");
            System.out.println("TextField Doc ESMD Transaction ID:"+suppDocesmdTransactionIdField.getRawValue());
            /*if(suppDocesmdTransactionIdField.getRawValue()!=null || !suppDocesmdTransactionIdField.getRawValue().isEmpty()) {

                cancel(event);
            }*/

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void cancel(UiNotificationEvent event) {
        close(WINDOW_CLOSE_ACTION);
    }




    @EventListener
    public void onUiNotificationEvent(UiNotificationEvent event) {

        try {
            System.out.println("Received  event id is " + event.getMessage());
            Long recvdId = Long.parseLong(event.getMessage());
            System.out.println("After parsing id is" + recvdId);
            System.out.println("Current form id is" + x12278SubmissionDc.getItem().getId());
            X12278Submission currItem = x12278SubmissionDc.getItem();

            System.out.println("Current X12278 Submission Supporting ESMD Transaction ID: " + currItem.getSuppDocesmdTransactionId());//null
            System.out.println("Current X12278 Submission Supporting Document Message: " + currItem.getSuppDocMessage());
            System.out.println("Current X12278 Submission PA Status: " + currItem.getStatus());

            if (recvdId.compareTo(x12278SubmissionDc.getItem().getId()) == 0) {

                try {

                    System.out.println("----------------------------------------------------------");
                    System.out.println("TextField Doc ESMD Transaction ID:"+suppDocesmdTransactionIdField.getRawValue());
                    if(currItem.getSuppDocesmdTransactionId()==null || currItem.getSuppDocesmdTransactionId().isEmpty()) {
                        System.out.println("-------------------Testing-------------------");
                        X12278Submission sub = dataManager.reload(x12278SubmissionDc.getItem(), "x12278Submission-view");

                        //Old Code
                        System.out.println("X12278 Submission Supporting ESMD Transaction ID: " + sub.getSuppDocesmdTransactionId());
                        System.out.println("X12278 Submission Supporting Document Message: " + sub.getSuppDocMessage());
                        System.out.println("X12278 Submission Submission PA Status: " + sub.getStatus());
                        /*if(sub.getSuppDocesmdTransactionId()==null || sub.getSuppDocesmdTransactionId().isEmpty())
                        {
                            if(currItem.getSuppDocesmdTransactionId()!=null || !currItem.getSuppDocesmdTransactionId().isEmpty()) {
                                x12278SubmissionsDl.setEntityId(currItem);
                                //submissionDl.load();
                                System.out.println("X12278 Submission string is " + x12278SubmissionsDl.getEntityId().toString());
                                System.out.println("get status change size" + sub.getPaStatusChange().size());
                                x12278SubmissionDc.setItem(currItem);
                                System.out.println("get status change size" + paStatusChangeDc.getItems().size());
                            }
                            else{*/
                                x12278SubmissionsDl.setEntityId(sub);
                                //submissionDl.load();
                                System.out.println("Submission string is " + x12278SubmissionsDl.getEntityId().toString());
                                System.out.println("get status change size" + sub.getPaStatusChange().size());
                                x12278SubmissionDc.setItem(sub);
                                System.out.println("get status change size" + paStatusChangeDc.getItems().size());
                         /*   }
                        }*/
                        /*
                        //x12278SubmissionsDl.setEntityId(sub);
                        //submissionDl.load();
                        System.out.println("Submission string is " + x12278SubmissionsDl.getEntityId().toString());
                        System.out.println("get status change size" + sub.getPaStatusChange().size());
                        x12278SubmissionDc.setItem(sub);
                        System.out.println("get status change size" + paStatusChangeDc.getItems().size());
                        */
                        System.out.println("get current pa document size: " + currItem.getPaDocument().size() + " updated pa Document size: " + sub.getPaDocument().size());
                        System.out.println("get current pa status change size: " + currItem.getPaStatusChange().size() + " updated pa status change size: " + sub.getPaStatusChange().size());
                        System.out.println("get current pa error size: " + currItem.getPaError().size() + " updated pa error size: " + sub.getPaError().size());
                        System.out.println("get current pa notification slot size: " + currItem.getPaNotificationSlot().size() + " updated pa notification slot size: " + sub.getPaNotificationSlot().size());

                        if (currItem.getPaDocument().size() < sub.getPaDocument().size()) {
                            System.out.println("Setting Items");
                            paDocumentTable.setItems(new ContainerTableItems<>(paDocumentDc));
                            //statusChangesTable.setSelected(sub.getStatusChange());
                        }
                        if (currItem.getPaStatusChange().size() > 0 || sub.getPaStatusChange().size() > 0) {
                            try {
                                if (currItem.getPaStatusChange().size() < sub.getPaStatusChange().size()) {
                                    System.out.println("Setting PA Status Change table");
                                    paStatusChangeTable.setItems(new ContainerTableItems<>(paStatusChangeDc));
                                    //statusChangesTable.setSelected(sub.getStatusChange());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (currItem.getPaError().size() > 0 || sub.getPaError().size() > 0) {
                            try {
                                if (currItem.getPaError().size() < sub.getPaError().size()) {
                                    System.out.println("Setting PA Error table");
                                    paErrorTable.setItems(new ContainerTableItems<>(paErrorDc));
                                    // errorTable.setSelected(sub.getError());
                                    // errorTable.setItems();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (currItem.getPaNotificationSlot().size() > 0 || sub.getPaNotificationSlot().size() > 0) {
                            try {
                                if (currItem.getPaNotificationSlot().size() < sub.getPaNotificationSlot().size()) {
                                    System.out.println("Setting PA Notification Slot table");
                                    paNotificationSlotTable.setItems(new ContainerTableItems<>(paNotificationSlotDc));
                                    // notificationSlotsTable.setSelected(sub.getNotificationSlot());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        /*
                        if (sub.getSuppDocesmdTransactionId() == null) {
                            try {
                                System.out.println("Setting SuppDocesmdTransactionId: " + sub.getSuppDocesmdTransactionId());
                                System.out.println("Setting Current SuppDocesmdTransactionId: " + currItem.getSuppDocesmdTransactionId());
                                if (currItem.getSuppDocesmdTransactionId() != null) {
                                    suppDocesmdTransactionIdField.setValue(currItem.getSuppDocesmdTransactionId());
                                    //sub.setSuppDocesmdTransactionId(currItem.getSuppDocesmdTransactionId());
                                    //dataManager.commit(sub);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        */

                        // OLD Code


                    /*
                    if(sub.getSuppDocMessage().equalsIgnoreCase("Success"))
                    {
                        try{
                            System.out.println("Outside Setting SuppDocMessage: "+sub.getSuppDocMessage());// Success
                            System.out.println("Outside Setting Current SuppDocMessage: "+currItem.getSuppDocMessage());
                            sub.setSuppDocMessage(currItem.getSuppDocMessage());
                            dataManager.commit(sub);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    */
                    /*if(currItem.getSuppDocMessage().equalsIgnoreCase(sub.getSuppDocMessage()))
                    {
                        System.out.println("Setting SuppDocMessage: "+sub.getSuppDocMessage());
                        System.out.println("Setting Current SuppDocMessage: "+currItem.getSuppDocMessage());
                        suppDocMessageField.setValue(currItem.getSuppDocMessage());
                        sub.setSuppDocMessage(currItem.getSuppDocMessage());
                    }
                    dataManager.commit(sub);

                    /*
                    if(!currItem.getStatus().equalsIgnoreCase(sub.getStatus()))
                    {
                        System.out.println("Setting X12278 Submission Status from DB: "+sub.getStatus());
                        statusField.setValue(sub.getStatus());
                    }
                    */
           /* if(!currItem.getStage().equals(sub.getStage())){
                notifications.create().withCaption("Notification Received").withType(Notifications.NotificationType.HUMANIZED).show();
            }
            //submissionDc.getView().
            System.out.println("updated value "+sub.getStage());*/
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

            } else {
                System.out.println("current item is null in uinotification event");
            }
        }
        catch(Exception e)
            {
                e.printStackTrace();
            }
    }



}