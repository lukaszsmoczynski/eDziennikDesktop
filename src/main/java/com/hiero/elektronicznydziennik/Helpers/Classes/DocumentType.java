package com.hiero.elektronicznydziennik.Helpers.Classes;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DocumentType {
    private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
    private final SimpleStringProperty mName = new SimpleStringProperty();

    public DocumentType(Integer id) {
        mId.set(id);
    }

    public int getId() {
        return mId.get();
    }

    public SimpleIntegerProperty idProperty() {
        return mId;
    }

    public String getName() {
        return mName.get();
    }

    public SimpleStringProperty nameProperty() {
        return mName;
    }

    public DocumentType setName(String name) {
        this.mName.set(name);
        return this;
    }
}
