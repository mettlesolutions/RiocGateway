package com.mettles.rioc.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ProvLastSubmittedTranxType implements EnumClass<String> {

    register("Register"),
    unregister("UnRegister");

    private String id;

    ProvLastSubmittedTranxType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ProvLastSubmittedTranxType fromId(String id) {
        for (ProvLastSubmittedTranxType at : ProvLastSubmittedTranxType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}