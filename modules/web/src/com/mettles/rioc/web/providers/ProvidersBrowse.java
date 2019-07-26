package com.mettles.rioc.web.providers;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Providers;

@UiController("rioc_providers.browse")
@UiDescriptor("providers-browse.xml")
@LookupComponent("providersesTable")
@LoadDataBeforeShow
public class ProvidersBrowse extends StandardLookup<Providers> {
}