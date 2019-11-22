package com.mettles.rioc.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TransType implements EnumClass<String> {
    X12("X12 Transaction"),
    XDR("XDR Transaction");

    private String id;

    TransType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TransType fromId(String id) {
        for (TransType at : TransType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}