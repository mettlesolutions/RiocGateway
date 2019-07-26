package com.mettles.rioc.web.hihconfiguration;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.HIHConfiguration;

@UiController("rioc_HIHConfiguration.browse")
@UiDescriptor("hih-configuration-browse.xml")
@LookupComponent("hIHConfigurationsTable")
@LoadDataBeforeShow
public class HIHConfigurationBrowse extends StandardLookup<HIHConfiguration> {
}