package com.mettles.rioc.service;

import com.mettles.rioc.X12275SubmissionStatus;
import com.mettles.rioc.core.PDFReadWriter;
import com.mettles.rioc.core.X12275ConnectSoapClient;
import com.mettles.rioc.entity.Document;
import com.mettles.rioc.entity.Providers;
import com.mettles.rioc.entity.Recepient;
import com.mettles.rioc.entity.Submission;
import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import com.mettles.rioc.core.DocSubmissionClient;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.UUID;

import org.sejda.core.notification.context.GlobalNotificationContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.core.service.TaskExecutionService;
import org.sejda.model.exception.SejdaRuntimeException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.PercentageOfWorkDoneChangedEvent;
import org.sejda.model.notification.event.TaskExecutionCompletedEvent;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.output.ExistingOutputPolicy;
import org.sejda.model.output.FileOrDirectoryTaskOutput;
import org.sejda.model.parameter.SplitBySizeParameters;



@Service(SubmissionService.NAME)
public class SubmissionServiceBean implements SubmissionService {
    @Inject
    private DocSubmissionClient soapclx;
    @Inject
    private DataManager datamanager;
    @Inject
    private Persistence persistence;


    @Inject
    private FileLoader fileLoader;

    @Inject
    private PDFReadWriter pdfreadwriter;


    @Override
    public List<Document> SplitDocuments(Document largeDoc){
        List<Document> retDocArryList = new ArrayList<>();
        System.out.println("Splitting the documents");
        SplitBySize(largeDoc,retDocArryList);
        System.out.println("After Splitting the document size"+retDocArryList.size());
    /*    try (Transaction tx = persistence.createTransaction()) {
            // get EntityManager for the current transaction
            EntityManager em = persistence.getEntityManager();
            // create and execute Query
             em.remove(largeDoc);
            // commit transaction
            tx.commit();
        }*/
        return retDocArryList;
    }


    @Override
    public X12275SubmissionStatus SubmitX12275Submission(Submission sub) {

        return AppBeans.get(X12275ConnectSoapClient.class).SubmitCAQHBatchSubmission(sub);

    }

    @Override
    public Submission SubmiteMDRRegFile(Providers prov, boolean bRegister) {

        Submission retVal = prov.getSubmissionID();
        System.out.println("reached rioc_submission service");
        soapclx = AppBeans.get(DocSubmissionClient.class);
        //try {

            retVal = soapclx.SubmiteMDRRequest(prov,bRegister);

       /* }catch(Exception e){
            System.out.println(e.toString());
        }*/




        return retVal;


    }

    @Override
    public void PDFReadWrite() {

        pdfreadwriter = AppBeans.get(PDFReadWriter.class);

        String sampletext = "Hello World! This is the first paragraph in the pdf";

       pdfreadwriter.writeEDItoXML();

        //System.out.println("Filename is "+filename);
        //String textwritten = pdfreadwriter.readPdf(filename);
        //System.out.println("Text Written is "+textwritten);

    }

    private void SplitBySize(Document largeDoc,List<Document> docArryList) {
        // configure the split by pages task
        SplitBySizeParameters taskParameters = new SplitBySizeParameters(75000000);
        String path = "C:\\Users\\paddu\\tmp";
        String uuid = UUID.randomUUID().toString();
        File targetFile = null;
        File indir = null;
        try {

            InputStream inputStream = fileLoader.openStream(largeDoc.getFileDescriptor());
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
             indir = new File(path + "\\" + uuid);
            if (!indir.exists()) indir.mkdirs();
             targetFile = new File(path + "\\" + uuid+ "base.pdf");

            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            inputStream.close();
            outStream.close();


        }catch (FileStorageException | IOException e ) {
            System.out.println("Exception occured while reading file");
            e.printStackTrace();

        }
        // which file should be split
        if(targetFile == null){
            System.out.println("Not Able to write to PDF file from the file descriptor");
            return;
        }
        taskParameters.addSource(PdfFileSource.newInstanceNoPassword(targetFile));


        // where to output PDF document results
        taskParameters.setExistingOutputPolicy(ExistingOutputPolicy.OVERWRITE);
        File dir = new File(path + "\\" + uuid+"\\"+"out");
        if (!dir.exists()) dir.mkdirs();
        taskParameters.setOutput(new FileOrDirectoryTaskOutput(dir));

        // register listeners to get events about progress, failure, completion.
       //registerTaskListeners();

        // execute the task
        TaskExecutionService taskExecutionService = new DefaultTaskExecutionService();
        taskExecutionService.execute(taskParameters);
        File [] files = dir.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) { //this line weeds out other directories/folders

                    System.out.println(files[i]);
                    Document dtTemp = new Document();
                    dtTemp.setFilename(files[i].getName());
                    dtTemp.setTitle(largeDoc.getTitle());
                    dtTemp.setAttachmentControlNumber(largeDoc.getAttachmentControlNumber());
                    dtTemp.setSplitNumber(i+1);
                    dtTemp.setLanguage(largeDoc.getLanguage());
                    dtTemp.setComments(largeDoc.getComments());
                    FileDescriptor fileDescriptor = datamanager.create(FileDescriptor.class);
                    fileDescriptor.setName(files[i].getName());
                    fileDescriptor.setExtension("pdf"); // As we will be supporting only pdf
                    fileDescriptor.setCreateDate(new Date());
                    try {
                        //byte[] bytes = files[i].
                        fileDescriptor.setSize((long) files[i].length());
                        byte[] bytesArray = new byte[(int)  files[i].length()];

                        FileInputStream fis = new FileInputStream(files[i]);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                        fileLoader.saveStream(fileDescriptor, () -> new ByteArrayInputStream(bytesArray));
                    } catch (FileStorageException | IOException e) {
                        System.out.println("error occured");
                        e.printStackTrace();
                    }

                    datamanager.commit(fileDescriptor);
                    dtTemp.setFileDescriptor(fileDescriptor);
                    docArryList.add(dtTemp);

                    files[i].delete();
                }
            }
            dir.delete();
        }else{
          System.out.println("Output files from sejda are empty");
          return;
        }
        targetFile.delete();
        indir.delete();
    }

    private static void registerTaskListeners() {
        GlobalNotificationContext.getContext().addListener(new ProgressListener());
        GlobalNotificationContext.getContext().addListener(new FailureListener());
        GlobalNotificationContext.getContext().addListener(new CompletionListener());
    }


    private static class ProgressListener implements EventListener<PercentageOfWorkDoneChangedEvent> {

        @Override
        public void onEvent(PercentageOfWorkDoneChangedEvent event) {
            System.out.println("Task progress: {}% done."+event.getPercentage().toPlainString());
        }
    }


    private static class FailureListener implements EventListener<TaskExecutionFailedEvent> {

        @Override
        public void onEvent(TaskExecutionFailedEvent event) {
            System.out.println("Task execution failed.");
            // rethrow it to the main
            throw new SejdaRuntimeException(event.getFailingCause());
        }
    }


    private static class CompletionListener implements EventListener<TaskExecutionCompletedEvent> {

        @Override
        public void onEvent(TaskExecutionCompletedEvent event) {
            System.out.println("Task completed in {} millis."+ event.getExecutionTime());
        }

    }
    @Override
    public Submission CallConnectApi(Submission sub, String SplitStr, String parentId, ArrayList<Document> docArryList) {
        Submission retVal = sub;
        System.out.println("reached rioc_submission service");
        soapclx = AppBeans.get(DocSubmissionClient.class);
        try {

            retVal = soapclx.SoapClientCall(sub,SplitStr,parentId,docArryList);

        }catch(Exception e){
            System.out.println(e.toString());
        }




        return retVal;
    }
}