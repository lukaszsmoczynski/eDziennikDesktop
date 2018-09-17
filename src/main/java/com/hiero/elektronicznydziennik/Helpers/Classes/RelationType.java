package com.hiero.elektronicznydziennik.Helpers.Classes;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RelationType {
    private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
    private final SimpleStringProperty mName = new SimpleStringProperty();

    public RelationType(Integer id) {
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

    public RelationType setName(String name) {
        this.mName.set(name);
        return this;
    }
}
