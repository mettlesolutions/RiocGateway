package com.mettles.rioc.web.recepient;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Recepient;

@UiController("rioc_Recepient.browse")
@UiDescriptor("recepient-browse.xml")
@LookupComponent("recepientsTable")
@LoadDataBeforeShow
public class RecepientBrowse extends StandardLookup<Recepient> {
}