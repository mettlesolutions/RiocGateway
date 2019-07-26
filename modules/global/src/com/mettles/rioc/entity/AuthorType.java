package com.mettles.rioc.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AuthorType implements EnumClass<String> {

    institution("Institution"),
    individual("Individual");

    private String id;

    AuthorType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AuthorType fromId(String id) {
        for (AuthorType at : AuthorType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}