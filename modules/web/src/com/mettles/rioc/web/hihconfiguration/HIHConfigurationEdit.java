package com.mettles.rioc.web.hihconfiguration;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.HIHConfiguration;

@UiController("rioc_HIHConfiguration.edit")
@UiDescriptor("hih-configuration-edit.xml")
@EditedEntityContainer("hIHConfigurationDc")
@LoadDataBeforeShow
public class HIHConfigurationEdit extends StandardEditor<HIHConfiguration> {
}