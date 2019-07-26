package com.mettles.rioc.web.lineofbusiness;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.LineofBusiness;

@UiController("rioc_LineofBusiness.edit")
@UiDescriptor("lineof-business-edit.xml")
@EditedEntityContainer("lineofBusinessDc")
@LoadDataBeforeShow
public class LineofBusinessEdit extends StandardEditor<LineofBusiness> {
}