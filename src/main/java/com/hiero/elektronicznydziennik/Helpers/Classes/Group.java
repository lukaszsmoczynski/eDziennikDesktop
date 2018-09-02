package com.hiero.elektronicznydziennik.Helpers.Classes;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Group {
    private final SimpleIntegerProperty mId = new SimpleIntegerProperty();
    private final SimpleStringProperty mName = new SimpleStringProperty();
    private final SimpleIntegerProperty mCreationYear = new SimpleIntegerProperty();
    private final Integer mParentGroupId;
    private Group mParentGroup;

    public Group(Integer id, Integer parentGroupId) {
        this.mId.set(id);
        mParentGroupId = parentGroupId;
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

    public Group setName(String name) {
        this.mName.set(name);
        return this;
    }

    public int getCreationYear() {
        return mCreationYear.get();
    }

    public SimpleIntegerProperty creationYearProperty() {
        return mCreationYear;
    }

    public Group setCreationYear(int creationYear) {
        this.mCreationYear.set(creationYear);
        return this;
    }

    public Group getParentGroup() {
        return mParentGroup;
    }

    public Group setParentGroup(Group parentGroup) {
        mParentGroup = parentGroup;
        return this;
    }

    public Integer getParentGroupId() {
        return mParentGroupId;
    }
}
