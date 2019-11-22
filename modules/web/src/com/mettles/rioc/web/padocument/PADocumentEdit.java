package com.mettles.rioc.web.padocument;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.mettles.rioc.entity.DocumentType;
import com.mettles.rioc.entity.PADocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.util.function.Consumer;

@UiController("rioc_PADocument.edit")
@UiDescriptor("pa-document-edit.xml")
@EditedEntityContainer("pADocumentDc")
@LoadDataBeforeShow
public class PADocumentEdit extends StandardEditor<PADocument> {

    @Inject
    private FileUploadField fileDescriptorField;

    @Inject
    protected LookupField<DocumentType> document_typeField;

    @Inject
    protected TextField filenameField;

    @Inject
    Button winCommitClose;

    @Inject
    Button winClose;

    @Inject
    private Notifications showNotification;

    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private Notifications notifications;

    @Subscribe
    protected void onInit(InitEvent event)  {

        fileDescriptorField.addFileUploadSucceedListener(uploadSucceedEvent -> {

            System.out.println("Uploaded Document Name: "+fileDescriptorField.getFileName());
            System.out.println("Uploaded Document Extension: "+FilenameUtils.getExtension(fileDescriptorField.getFileName()));
            System.out.println("Uploaded Document Type: "+document_typeField.getValue());
            System.out.println("Uploaded Document Type ID: "+document_typeField.getValue().getId());

            if (!FilenameUtils.getExtension(fileDescriptorField.getFileName()).equalsIgnoreCase(document_typeField.getValue().getId())) {
                notifications.create().withCaption("Uploaded Document: " + fileDescriptorField.getFileName() + " and Document Type: " + document_typeField.getValue() + " is not matched").show();
                winCommitClose.setEnabled(false);
            }
            else {
                System.out.println("Uploaded File Extension: "+fileDescriptorField.getFileName()+" Document Type: "+document_typeField.getValue().getId()+" is matched");
                //System.out.println("filenameField Value: "+filenameField);
                winCommitClose.setEnabled(true);
                winCommitClose.setVisible(true);
                /*if(filenameField!=null && !filenameField.isEmpty()) {
                    if (!filenameField.getRawValue().isEmpty()) {
                        System.out.println("Uploaded File Extension: "+fileDescriptorField.getFileName());
                        System.out.println("Provided Document File Name in Text Field: "+filenameField.getRawValue());
                        if (!fileDescriptorField.getFileName().equalsIgnoreCase(filenameField.getRawValue())) {
                            notifications.create().withCaption("Uploaded Document: " + fileDescriptorField.getFileName() + " and provided filename: " + filenameField.getRawValue() + " is not matched").show();
                            winCommitClose.setEnabled(false);
                        }
                        else {
                            System.out.println("fileDescriptorField.getFileName() and filenameField.getRawValue() are Same Condition");
                            winCommitClose.setEnabled(true);
                            winCommitClose.setVisible(true);
                        }
                    }
                    else {
                        //System.out.println("filenameField.getRawValue() is Empty COndition");
                        winCommitClose.setEnabled(true);
                        winCommitClose.setVisible(true);
                    }

                }*/
            /*
                else {
                    //System.out.println("filenameField is Null and Empty COndition");
                    winCommitClose.setEnabled(true);
                    winCommitClose.setVisible(true);
                }
                */
            }

        });

        fileDescriptorField.addFileUploadErrorListener(uploadErrorEvent ->
                notifications.create()
                        .withCaption("File upload error, Kindly try Again")
                        .show());


        /*filenameField.addValueChangeListener(stringValueChangeEvent -> {
            try {
                if(fileDescriptorField!=null && !fileDescriptorField.isEmpty()) {
                    if (!fileDescriptorField.getFileName().isEmpty()) {
                        System.out.println("Provided File Descriptor File Name: " + fileDescriptorField.getFileName());
                        System.out.println("Provided File Name: " + filenameField.getRawValue());
                        if (!fileDescriptorField.getFileName().equalsIgnoreCase(filenameField.getRawValue())) {
                            notifications.create().withCaption("provided filename: " + filenameField.getRawValue() + " and Uploaded Document: " + fileDescriptorField.getFileName() + " is not matched").show();
                            winCommitClose.setEnabled(false);
                        } else {
                            System.out.println("fileDescriptorField.getFileName() and filenameField.getRawValue() are equal COndition");
                            winCommitClose.setEnabled(true);
                            winCommitClose.setVisible(true);
                        }
                    } else {
                        //System.out.println("fileDescriptorField.getFileName() is Empty COndition");
                        winCommitClose.setEnabled(true);
                        winCommitClose.setVisible(true);
                    }
                }
                else {
                    //System.out.println("fileDescriptorField is NUll and Empty COndition");
                    winCommitClose.setEnabled(true);
                    winCommitClose.setVisible(true);
                    //winClose.setEnabled(true);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });
        */

        document_typeField.addValueChangeListener(stringValueChangeEvent -> {
            try {
                if(fileDescriptorField!=null && !fileDescriptorField.isEmpty()) {
                    if (!fileDescriptorField.getFileName().isEmpty()) {
                        System.out.println("Provided File Descriptor File Name: " + fileDescriptorField.getFileName());
                        System.out.println("Uploaded Document Type: " + document_typeField.getValue().getId());
                        if (!document_typeField.getValue().getId().equalsIgnoreCase(FilenameUtils.getExtension(fileDescriptorField.getFileName()))) {
                            notifications.create().withCaption("Document Type: " + document_typeField.getValue().getId() + " and Uploaded Document: " + fileDescriptorField.getFileName() + " is not matched").show();
                            winCommitClose.setEnabled(false);
                        } else {
                            System.out.println("document_typeField.getValue().getId() and fileDescriptorField.getFileName() extension is same Condition");
                            winCommitClose.setEnabled(true);
                            winCommitClose.setVisible(true);
                            //winClose.setEnabled(true);
                        }
                    }
                    else {
                        //System.out.println("fileDescriptorField.getFileName() is empty COndition");
                        winCommitClose.setEnabled(true);
                        winCommitClose.setVisible(true);
                    }
                } else {
                    //System.out.println("fileDescriptorField is NUll and Empty COndition");
                    winCommitClose.setEnabled(true);
                    winCommitClose.setVisible(true);
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        });
    }



}

