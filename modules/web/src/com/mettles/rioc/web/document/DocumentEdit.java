package com.mettles.rioc.web.document;


import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Document;


@UiController("rioc_Document.edit")
@UiDescriptor("document-edit.xml")
@EditedEntityContainer("documentDc")
@LoadDataBeforeShow
public class DocumentEdit extends StandardEditor<Document> {

}