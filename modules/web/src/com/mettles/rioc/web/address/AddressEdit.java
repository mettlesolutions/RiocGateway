package com.mettles.rioc.web.address;

import com.haulmont.cuba.gui.screen.*;
import com.mettles.rioc.entity.Address;

@UiController("rioc_Address.edit")
@UiDescriptor("address-edit.xml")
@EditedEntityContainer("addressDc")
@LoadDataBeforeShow
public class AddressEdit extends StandardEditor<Address> {
}