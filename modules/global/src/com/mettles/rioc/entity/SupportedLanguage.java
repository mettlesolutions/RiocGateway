package com.mettles.rioc.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SupportedLanguage implements EnumClass<String> {

    English("en-us"),
    Espanol("es-us");

    private String id;

    SupportedLanguage(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SupportedLanguage fromId(String id) {
        for (SupportedLanguage at : SupportedLanguage.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}