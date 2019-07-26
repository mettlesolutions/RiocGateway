package com.mettles.rioc.web.recepient;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Recepient;

@UiController("rioc_Recepient.edit")
@UiDescriptor("recepient-edit.xml")
@EditedEntityContainer("recepientDc")
@LoadDataBeforeShow
public class RecepientEdit extends StandardEditor<Recepient> {
}