package com.mettles.rioc.web.lineofbusiness;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.LineofBusiness;

@UiController("rioc_LineofBusiness.browse")
@UiDescriptor("lineof-business-browse.xml")
@LookupComponent("lineofBusinessesTable")
@LoadDataBeforeShow
public class LineofBusinessBrowse extends StandardLookup<LineofBusiness> {
}